package com.project.api.dao;

import com.project.api.entity.Basket;
import com.project.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    List<Basket> findByUser(User user);
}
