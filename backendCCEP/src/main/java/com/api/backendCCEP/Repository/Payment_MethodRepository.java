package com.api.backendCCEP.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Payment_Method;

@Repository
public interface Payment_MethodRepository extends JpaRepository<Payment_Method, Long> {

    Optional<Payment_Method> findById(Integer integer);

}
