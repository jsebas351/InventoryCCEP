package com.api.backendCCEP.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}
