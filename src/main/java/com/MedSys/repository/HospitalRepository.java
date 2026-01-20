package com.MedSys.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MedSys.entity.Hospital;
import com.MedSys.entity.User;


@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long>{

	Optional<Hospital> findByName(String username);

	List<Hospital> findByCreatedById(Long id);


}
