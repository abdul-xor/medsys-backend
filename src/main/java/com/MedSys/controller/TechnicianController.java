package com.MedSys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.MedSys.entity.Maintenance;
import com.MedSys.service.MaintenanceService;

@RestController
public class TechnicianController {
	
	@Autowired 
    private MaintenanceService maintenanceService;
 
    @GetMapping("/api/technician/maintenance")
    public ResponseEntity<List<Maintenance>> getAllMaintenance()
    {
        // Get all maintenance records and return them with status code 200 OK;
        return ResponseEntity.status(200).body(maintenanceService.getAllMaintenance());
    }

    @PutMapping("/api/technician/maintenance/update/{maintenanceId}")
    public ResponseEntity<Maintenance> updateMaintenance(@PathVariable Long maintenanceId, @RequestBody Maintenance updatedMaintenance) 
    {
        // Update the maintenance record with the given id and return updated record with status code 200 OK;
        return ResponseEntity.status(200).body(maintenanceService.updateMaintenance(maintenanceId,updatedMaintenance));
    }

    @DeleteMapping("/api/technician/maintenance/{maintenanceId}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long maintenanceId)
    {
        maintenanceService.deleteMaintenance(maintenanceId);
        return ResponseEntity.status(200).build();
    }

}
