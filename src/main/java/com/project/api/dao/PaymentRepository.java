package com.project.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.api.entity.PaymentEntity;
@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity,Integer> {}
