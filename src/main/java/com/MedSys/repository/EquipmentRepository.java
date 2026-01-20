package com.MedSys.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MedSys.entity.Equipment;
@Repository
public interface  EquipmentRepository  extends JpaRepository<Equipment, Long>{

	List<Equipment> findByHospitalId(Long hospitalId);

}
