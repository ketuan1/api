package com.project.api.controller;

import com.project.api.dao.BasketItemRepository;
import com.project.api.dao.BasketRepository;
import com.project.api.dao.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/baskets")
@CrossOrigin("http://localhost:3000")
public class BasketController {
    private final ProductRepository productRepository;

    private final BasketItemRepository basketItemRepository;

    private final BasketRepository basketRepository;

    @Autowired

    public BasketController(ProductRepository productRepository,
                            BasketItemRepository basketItemRepository,
                            BasketRepository basketRepository) {
        this.productRepository = productRepository;
        this.basketItemRepository = basketItemRepository;
        this.basketRepository = basketRepository;
    }
}
