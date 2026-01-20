package com.MedSys.service;
import java.util.List;

//import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.MedSys.entity.Hospital;
import com.MedSys.entity.User;
import com.MedSys.repository.HospitalRepository;
import com.MedSys.repository.UserRepository;


@Service
public class HospitalService {
	
	@Autowired
	private HospitalRepository hospitalRepository;
	
	
	@Autowired UserRepository userRepository;
	
	 public Hospital createHospital(Hospital hospital){
	        return hospitalRepository.save(hospital);
	  }
	 
	 public Hospital createHospitalForLoggedInUser(Hospital hospital) {

		    // Get logged-in username
		    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    String username = auth.getName();

		    // Fetch user directly
		    User user = userRepository.findByUsername(username);

		    if (user == null) {
		        throw new RuntimeException("User not found");
		    }

		    // Set the logged-in user as creator
		    hospital.setCreatedBy(user);

		    // Save hospital
		    return hospitalRepository.save(hospital);
		}


	 public List<Hospital> getAllHospitals(){
	        return hospitalRepository.findAll();
	  }


	
	
	

}
