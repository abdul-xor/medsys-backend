package com.MedSys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MedSys.entity.Maintenance;


@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long>{

}
