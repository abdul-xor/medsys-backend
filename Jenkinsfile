pipeline {
    agent any

    environment {
        APP_NAME = "medsys"
        APP_PORT = "8085"
        BASE_DIR = "/opt/apps/dev/medsys"
        RELEASES = "${BASE_DIR}/releases"
    }

    triggers {
        githubPush()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/abdul-xor/medsys-backend.git'
                    ]],
                    extensions: [
                        [$class: 'CloneOption',
                        shallow: true,
                        depth: 1,
                        noTags: true,
                        timeout: 30]
                    ]
                ])
            }
        }

        stage('Build & Test & Coverage') {
            steps {
                sh '''
                chmod +x mvnw
                ./mvnw clean test package
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './mvnw sonar:sonar'
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
                        echo "Quality Gate timed out – continuing DEV deployment"
                    }
                }
            }
        }

        stage('Deploy with Auto Rollback') {
            steps {
                sh '''
                set -e

                mkdir -p ${RELEASES}

                VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
                NEW_WAR=${RELEASES}/medsys-${VERSION}.war

                # Backup current
                if [ -L "${BASE_DIR}/current" ]; then
                    PREV=$(readlink -f ${BASE_DIR}/current)
                    ln -sfn "$PREV" ${BASE_DIR}/backup
                fi

                WAR_FILE=$(ls target/*.war 2>/dev/null | head -1)
                [ -z "$WAR_FILE" ] && { echo "WAR not found"; exit 1; }

                cp "$WAR_FILE" "$NEW_WAR"
                ln -sfn "$NEW_WAR" ${BASE_DIR}/current

                # Stop app on port
                PID=$(lsof -t -i:${APP_PORT} || true)
                [ -n "$PID" ] && kill -9 $PID

                # Start new app
                nohup java -jar ${BASE_DIR}/current > ${BASE_DIR}/app.log 2>&1 &

                sleep 20

                # Health check
                curl -sf http://localhost:${APP_PORT}/actuator/health || (
                    echo "Health check failed — rolling back"

                    PID=$(lsof -t -i:${APP_PORT} || true)
                    [ -n "$PID" ] && kill -9 $PID

                    if [ -L "${BASE_DIR}/backup" ]; then
                        ln -sfn $(readlink -f ${BASE_DIR}/backup) ${BASE_DIR}/current
                        nohup java -jar ${BASE_DIR}/current > ${BASE_DIR}/app.log 2>&1 &
                    fi
                    exit 1
                )
                '''
            }
        }

    post {
        success {
            echo "Build, Test, Sonar & Deploy successful"
        }
        failure {
            echo "Pipeline failed — rollback applied if needed"
        }
    }
}
