package com.MedSys.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MedSys.entity.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	 User findByUsername(String username);
	 
	

}
