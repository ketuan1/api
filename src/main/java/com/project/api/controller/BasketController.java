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
import java.util.Set;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:3000",allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("api/baskets")
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
        List<BasketItemDto> basketItemDtoList = basketItemRepository.findByBasketId(basketList.get(0).getId()).stream().map(item -> new BasketItemDto(
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
            basketRepository.save(basket);
        } else {
            basket = basketList.get(0);
        }

        int existingQuantity = product.getUnitsInStock();
        if (quantity > existingQuantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This product not enough quantity");
        }

        BasketItem basketItem = basketItemRepository.findByBasketIdAndProducts(basket.getId(),product);
        if(basketItem==null){
            basketItem = new BasketItem();
        }
        basketItem.setBasket(basket);
        basketItem.setProducts(product);
        basketItem.setQuantity(basketItem.getQuantity()+quantity);
        if (basketItem.getQuantity()<=0){
            basketItemRepository.delete(basketItem);
            List<BasketItemDto> lsBacketItemDto = convert(basketItemRepository.findByBasketId(basketList.get(0).getId()));
            BasketDto basketDto = convert(basket,lsBacketItemDto);
            return new ResponseEntity<>(basketDto, HttpStatus.OK);
        }else {
        basketItemRepository.save(basketItem);
        // This is when add product to basket, product UnitsInStock will reduce equal quantity
        product.setUnitsInStock(product.getUnitsInStock() - quantity);
        productRepository.save(product);
        List<BasketItemDto> lsBacketItemDto = convert(basketItemRepository.findByBasketId(basketList.get(0).getId()));
        BasketDto basketDto = convert(basket,lsBacketItemDto);
        return new ResponseEntity<>(basketDto, HttpStatus.OK);
        }
    }
    private List<BasketItemDto> convert(List<BasketItem> basketItemList){
        return basketItemList.stream().map(item -> new BasketItemDto(
                        item.getProducts().getId(),
                        item.getProducts().getName(),
                        item.getProducts().getPrice(),
                        item.getProducts().getImageUrl(),
                        item.getProducts().getCategory().getCategoryName(),
                        item.getQuantity()
                ))
                .sorted(Comparator.comparingLong(i -> i.getProductId()))
                .collect(Collectors.toList());
    }
    private BasketDto convert(Basket basket, List<BasketItemDto> basketItemDtoList){
        BasketDto basketDto = new BasketDto();
        basketDto.setId(basket.getId());
        basketDto.setEmail(basket.getUser().getEmail());
        basketDto.setBasketItem(basketItemDtoList);
        return basketDto;
    }
}
