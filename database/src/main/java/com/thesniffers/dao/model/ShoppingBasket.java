package com.thesniffers.dao.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "shopping_basket")
public class ShoppingBasket {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private ZonedDateTime created;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BasketStatus status;

    @Column(name = "status_date", nullable = false)
    private ZonedDateTime statusDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "shoppingBasket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;
}
