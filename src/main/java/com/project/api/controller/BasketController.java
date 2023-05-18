package com.project.api.controller;

import com.project.api.dao.BasketItemRepository;
import com.project.api.dao.BasketRepository;
import com.project.api.dao.ProductRepository;
import com.project.api.dao.UserRepository;
import com.project.api.dto.BasketDto;
import com.project.api.dto.BasketItemDto;
import com.project.api.entity.Basket;
import com.project.api.entity.BasketItem;
import com.project.api.entity.Product;
import com.project.api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/baskets")
@CrossOrigin("http://localhost:3000")
public class BasketController {
    private final ProductRepository productRepository;

    private final BasketItemRepository basketItemRepository;

    private final BasketRepository basketRepository;

    private final UserRepository userRepository;

    @Autowired

    public BasketController(ProductRepository productRepository,
                            BasketItemRepository basketItemRepository,
                            BasketRepository basketRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.basketItemRepository = basketItemRepository;
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BasketDto> getBasket(@PathVariable("userId") Long userId) {
        User user = userRepository.findById(userId).get();
        List<Basket> basketList = basketRepository.findByUser(user);

        if (basketList.isEmpty()) {
            throw new NoResultException("Can't found the basket");
        }

        List<BasketItemDto> basketItemDtoList = basketList.get(0).getBasketItems().stream()
                .map(item -> new BasketItemDto(
                        item.getProducts().getId(),
                        item.getProducts().getName(),
                        item.getProducts().getPrice(),
                        item.getProducts().getImageUrl(),
                        item.getProducts().getCategory().getCategoryName(),
                        item.getQuantity()
                ))
                .sorted(Comparator.comparingLong(i -> i.getProductId()))
                .collect(Collectors.toList());

        BasketDto basketDto = new BasketDto();
        basketDto.setId(basketList.get(0).getId());
        basketDto.setEmail(basketList.get(0).getUser().getEmail());
        basketDto.setBasketItem(basketItemDtoList);

        return new ResponseEntity<>(basketDto, HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<BasketDto> addItemToBasket(@PathVariable("userId") Long userId,
                                                     @RequestParam("quantity") int quantity,
                                                     @RequestParam("productId") Long productId,
                                                     HttpServletResponse response) {
        Product product = productRepository.findById(productId).get();
        User user = userRepository.findById(userId).get();

        List<Basket> basketList = basketRepository.findByUser(user);
        Basket basket;

        if (basketList == null || basketList.isEmpty()) {
            basket = new Basket(user);

            Cookie cookie = new Cookie("email", user.getEmail());
            cookie.setMaxAge(30 * 24 * 60 * 60); // expired in 30 days
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            basket = basketList.get(0);
        }

        int existingQuantity = product.getUnitsInStock();
        if (quantity > existingQuantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This product not enough quantity");
        }


        basket.addItem(product, quantity);
        Basket returnBasket = basketRepository.save(basket);

        // This is when add product to basket, product UnitsInStock will reduce equal quantity
        product.setUnitsInStock(product.getUnitsInStock() - quantity);
        productRepository.save(product);

        // Transform to DTO
        List<BasketItemDto> basketItemDtoList = returnBasket.getBasketItems().stream()
                .map(item -> new BasketItemDto(
                        item.getProducts().getId(),
                        item.getProducts().getName(),
                        item.getProducts().getPrice(),
                        item.getProducts().getImageUrl(),
                        item.getProducts().getCategory().getCategoryName(),
                        item.getQuantity()
                ))
                .sorted(Comparator.comparingLong(i -> i.getProductId()))
                .collect(Collectors.toList());

        BasketDto basketDto = new BasketDto();
        basketDto.setId(returnBasket.getId());
        basketDto.setEmail(returnBasket.getUser().getEmail());
        basketDto.setBasketItem(basketItemDtoList);

        return new ResponseEntity<>(basketDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<BasketDto> removeBasketItem(@PathVariable("userId") Long userId,
                                                      @RequestParam("productId") Long productId,
                                                      @RequestParam("quantity") int quantity) {
        User user =  userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();
        List<Basket> basketList = basketRepository.findByUser(user);

        if (basketList.isEmpty()) {
            throw new NoResultException("can't find any basket");
        }

        Basket basket = basketList.get(0);
        BasketItem existingItem = basket.getBasketItems().stream()
                .filter(i -> i.getProducts().getId().equals(productId))
                .findAny().orElse(null);

        if (existingItem == null) {
            throw new NoResultException("Basket no this item");
        }

        // If quantity over 1 only reduce quantity, else equal 0 will remove from basket
        int newQuantity = existingItem.getQuantity() - quantity;
        existingItem.setQuantity(newQuantity);

        if (newQuantity == 0) {
            basket.getBasketItems().remove(existingItem);
            basketItemRepository.delete(existingItem);
        }

        // This is when remove product, product UnitsInStock will increase equal quantity
        product.setUnitsInStock(product.getUnitsInStock() + quantity);
        productRepository.save(product);

        Basket returnBasket = basketRepository.save(basket);

        // Transform to DTO
        List<BasketItemDto> basketItemDtoList = returnBasket.getBasketItems().stream()
                .map(item -> new BasketItemDto(
                        item.getProducts().getId(),
                        item.getProducts().getName(),
                        item.getProducts().getPrice(),
                        item.getProducts().getImageUrl(),
                        item.getProducts().getCategory().getCategoryName(),
                        item.getQuantity()
                )).collect(Collectors.toList());

        BasketDto basketDto = new BasketDto();
        basketDto.setId(returnBasket.getId());
        basketDto.setEmail(returnBasket.getUser().getEmail());
        basketDto.setBasketItem(basketItemDtoList);

        return new ResponseEntity<>(basketDto, HttpStatus.OK);
    }
}
