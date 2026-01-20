package com.MedSys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MedSys.entity.Maintenance;
import com.MedSys.entity.Order;
import com.MedSys.entity.User;
import com.MedSys.repository.HospitalRepository;
import com.MedSys.repository.UserRepository;
import com.MedSys.service.EquipmentService;
import com.MedSys.service.HospitalService;
import com.MedSys.service.MaintenanceService;
import com.MedSys.service.OrderService;
import com.MedSys.entity.Equipment;
import com.MedSys.entity.Hospital;

@RestController
@RequestMapping
public class HospitalController {
	@Autowired
	HospitalService hospitalService;
	
	@Autowired
	HospitalRepository hospitalRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EquipmentService equipmentService;
	
	@Autowired
	MaintenanceService maintenanceService;
	
	@Autowired
	OrderService orderService;
	
	
	@PostMapping("/api/hospital/create")
	public ResponseEntity<Hospital> createHospital(@RequestBody Hospital hospital){
		return ResponseEntity.status(201).body(hospitalService.createHospital(hospital));
	}
	
	
	@PostMapping("/api/hospital/my/create")
	public Hospital createMyHospital(@RequestBody Hospital hospital){
	    return hospitalService.createHospitalForLoggedInUser(hospital);
	}


	@GetMapping("/api/hospital")
	public ResponseEntity<List<Hospital> > getAllHospitals(){
		return ResponseEntity.status(200).body(hospitalService.getAllHospitals());
	}
	
	@GetMapping("/api/hospital/my")
    public ResponseEntity<List<Hospital>> getMyHospitals() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }

        List<Hospital> hospitals = hospitalRepository.findByCreatedById(user.getId());

        return ResponseEntity.ok(hospitals);
    }
	
		
	 @PostMapping("/api/hospital/equipment")
	 public ResponseEntity<Equipment> addEquipment(@RequestParam Long hospitalId, @RequestBody Equipment equipment) {
	     Equipment addedEquipment = equipmentService.addEquipment(hospitalId, equipment);
	     return new ResponseEntity<>(addedEquipment, HttpStatus.CREATED);
	 }
	 
	
	 @GetMapping("/api/hospital/equipment/{hospitalId}")
	 public ResponseEntity<List<Equipment>> getAllEquipmentOfHospital(@PathVariable Long hospitalId) {
	        List<Equipment> equipmentList = equipmentService.getAllEquipmentOfHospital(hospitalId);
	        return new ResponseEntity<>(equipmentList, HttpStatus.OK);
	 }
	 
	 @PostMapping("/api/hospital/maintenance/schedule")
	    public ResponseEntity<Maintenance> scheduleMaintenance(@RequestParam Long equipmentId, @RequestBody Maintenance maintenance) {
	        // schedule maintenance for the equipment and return the scheduled maintenance with status code 201 = CREATED;
	        return ResponseEntity.status(201).body(maintenanceService.scheduleMaintenance(equipmentId,maintenance));
	    }
	 
	 @PostMapping("/api/hospital/order")
	    public ResponseEntity<Order> placeOrder(@RequestParam Long equipmentId, @RequestBody Order order) 
	    {
	        Order placedOrder = orderService.placeOrder(equipmentId, order);
	        return new ResponseEntity<>(placedOrder,HttpStatus.CREATED);
	    }
	 
//	 @GetMapping("/api/hospital/getMyhospital")
//	 public ResponseEntity<Hospital> getMyHospital(Authentication authentication) {
//		    String username = authentication.getName(); // from JWT
//
//		    // Find hospital whose name matches the username (or some other mapping)
//		    Hospital hospital = hospitalRepository.findByName(username)
//		            .orElseThrow(() -> new RuntimeException("Hospital not found"));
//
//		    return ResponseEntity.ok(hospital);
//		}
	 
}
