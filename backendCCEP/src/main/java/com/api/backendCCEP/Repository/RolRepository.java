package com.api.backendCCEP.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.backendCCEP.Model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

}
