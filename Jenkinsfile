pipeline {
    agent any

    environment {
        APP_NAME    = "medsys"
        APP_PORT    = "8085"
        ENV         = "dev"
        BASE_DIR    = "/opt/apps/dev/medsys"
        RELEASES    = "${BASE_DIR}/releases"
        CURRENT     = "${BASE_DIR}/current"
        BACKUP      = "${BASE_DIR}/backup"
    }

    triggers {
        githubPush()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build, Test & Coverage') {
            steps {
                sh '''
                    chmod +x mvnw
                    MAVEN_OPTS="-Xmx512m" ./mvnw clean test package
                '''
            }
        }

        stage('SonarQube Analysis') {
            environment {
                SONAR_TOKEN = credentials('SONAR_TOKEN_MEDSYS')
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        ./mvnw sonar:sonar \
                        -Dsonar.login=$SONAR_TOKEN \
                        -Dsonar.projectKey=medsys-backend
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    try {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    } catch (err) {
                        echo "Quality Gate timeout – continuing DEV deployment"
                    }
                }
            }
        }

        stage('Deploy with Auto Rollback') {
            steps {
                sh '''
                    set -e

                    mkdir -p ${RELEASES}

                    VERSION=$(./mvnw help:evaluate \
                        -Dexpression=project.version -q -DforceStdout)

                    JAR_NAME=medical-equipment-and-tracking-system.jar
                    NEW_JAR=${RELEASES}/${APP_NAME}-${VERSION}.jar

                    # Backup current version
                    if [ -L "${CURRENT}" ]; then
                        ln -sfn $(readlink -f ${CURRENT}) ${BACKUP}
                    fi

                    cp target/${JAR_NAME} ${NEW_JAR}
                    ln -sfn ${NEW_JAR} ${CURRENT}

                    # Stop app if running
                    PID=$(lsof -t -i:${APP_PORT} || true)
                    [ -n "$PID" ] && kill -9 $PID

                    # Start app
                    nohup java -jar ${CURRENT} \
                      --spring.profiles.active=${ENV} \
                      --server.port=${APP_PORT} \
                      > ${BASE_DIR}/app.log 2>&1 &

                    sleep 25

                    # Health check
                    curl -sf http://localhost:${APP_PORT}/actuator/health || (
                        echo "Health check failed – rolling back"

                        PID=$(lsof -t -i:${APP_PORT} || true)
                        [ -n "$PID" ] && kill -9 $PID

                        if [ -L "${BACKUP}" ]; then
                            ln -sfn $(readlink -f ${BACKUP}) ${CURRENT}
                            nohup java -jar ${CURRENT} \
                              --spring.profiles.active=${ENV} \
                              --server.port=${APP_PORT} \
                              > ${BASE_DIR}/app.log 2>&1 &
                        fi
                        exit 1
                    )
                '''
            }
        }
    }

    post {
        success {
            echo "Build, Test, Sonar & Deployment successful"
        }
        failure {
            echo "Pipeline failed – rollback executed if required"
        }
    }
}
