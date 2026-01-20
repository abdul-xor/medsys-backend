package com.MedSys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MedSys.entity.Equipment;
import com.MedSys.entity.Hospital;
import com.MedSys.repository.EquipmentRepository;
import com.MedSys.repository.HospitalRepository;


@Service
public class EquipmentService {
	
	@Autowired
	EquipmentRepository equipmentRepository;
	
	@Autowired
	HospitalRepository hospitalRepository;
	
	 public Equipment addEquipment(Long hospitalId, Equipment equipment) {
	        Hospital hospital = hospitalRepository.findById(hospitalId)
	                .orElseThrow(() -> new RuntimeException("Hospital not found"));
	        equipment.setHospital(hospital);
	        return equipmentRepository.save(equipment);
	    }
	 
	 public List<Equipment> getAllEquipmentOfHospital(Long hospitalId) {
	        return equipmentRepository.findByHospitalId(hospitalId);
	    }

}
