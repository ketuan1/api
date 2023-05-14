package com.project.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "product")
@Setter
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "units_in_stock")
    private int unitsInStock;

    @Column(name = "date_created")
    @CreationTimestamp
    private Date dateCreated;

    @Column(name = "last_updated")
    @CreationTimestamp
    private Date lastUpdated;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BasketItem> basketItems;

    public Product() {
    }

    public Product(Long id, String name, BigDecimal price, String description, String imageUrl, int unitsInStock, Date dateCreated, Date lastUpdated, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unitsInStock = unitsInStock;
        this.dateCreated = dateCreated;
        this.lastUpdated = lastUpdated;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", unitsInStock=" + unitsInStock +
                ", dateCreated=" + dateCreated +
                ", lastUpdated=" + lastUpdated +
                ", category=" + category.toString() +
                '}';
    }
}
