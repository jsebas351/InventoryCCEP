package com.api.backendCCEP.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Loss;

@Repository
public interface LossRepository extends JpaRepository<Loss, Long>{

}
