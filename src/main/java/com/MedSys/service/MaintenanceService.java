package com.MedSys.service;

import java.util.List;

//import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MedSys.entity.Equipment;
import com.MedSys.entity.Maintenance;
import com.MedSys.repository.EquipmentRepository;
import com.MedSys.repository.MaintenanceRepository;


@Service
public class MaintenanceService {
	
	@Autowired
	MaintenanceRepository maintenanceRepository;
	
	@Autowired
	EquipmentRepository equipmentRepository;

	 public Maintenance scheduleMaintenance(Long equipmentId,Maintenance maintenance)
	    {
	        Equipment equipment=equipmentRepository.findById(equipmentId).orElse(null);
	        if(equipment!=null)
	        {
	            maintenance.setEquipment(equipment);
	            return maintenanceRepository.save(maintenance);
	        }
	        return null;
	    }

	 public List<Maintenance>  getAllMaintenance() {
		return maintenanceRepository.findAll();
	 }
	 
	 public Maintenance updateMaintenance(Long maintenanceId, Maintenance updatedMaintenance)
	    {
	        Maintenance maintenance=maintenanceRepository.findById(maintenanceId).orElse(null);
	        if(maintenance!=null)
	        {
	           updatedMaintenance.setId(maintenance.getId());
	           return maintenanceRepository.save(updatedMaintenance);    
	        }
	        return null;
	    }

	 public void deleteMaintenance(Long maintenanceId) {
		// TODO Auto-generated method stub
		 this.maintenanceRepository.deleteById(maintenanceId);
		
	 }  
	 
	 

}
