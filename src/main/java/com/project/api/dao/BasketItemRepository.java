package com.project.api.dao;

import com.project.api.entity.BasketItem;
import com.project.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
    List<BasketItem> findByBasketId(Long basketId);
    BasketItem findByBasketIdAndProducts(Long basketId, Product product);
}
