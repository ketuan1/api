package com.project.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.api.entity.Register;

@Repository
public interface RegisterRepository extends JpaRepository<Register, Long> {
    List<Register> findByEmail(String name);
}
