package com.project.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "basket_item")
@Getter
@Setter
public class BasketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Basket.class)
    @JoinColumn(name = "basket_id", referencedColumnName = "id")
    @JsonIgnore
    private Basket basket;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Product.class)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @JsonIgnore
    private Product products;

    private int quantity;

    public BasketItem() {
    }

    public BasketItem(Basket basket, Product products, int quantity) {
        this.basket = basket;
        this.products = products;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "BasketItem{" +
                "id=" + id +
                ", basket=" + basket +
                ", product=" + products +
                ", quantity=" + quantity +
                '}';
    }
}
