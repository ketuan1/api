package com.project.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "basket")
@Getter
@Setter
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BasketItem> basketItems;

    public void addItem(Product product, int quantity) {
        if (product != null) {
            if (basketItems == null) {
                basketItems = new HashSet<>();
            }

            BasketItem existingItem = basketItems.stream().filter(item -> item.getProducts().getId()
                            .equals(product.getId()))
                            .findAny()
                            .orElse(null);

            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                existingItem.setQuantity(newQuantity);
            } else {
                basketItems.add(new BasketItem(this, product, quantity));
            }
        }
    }

    public Basket() {
    }

    public Basket(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Basket{" +
                "id=" + id +
                ", user=" + user +
                ", basketItems=" + basketItems +
                '}';
    }
}
