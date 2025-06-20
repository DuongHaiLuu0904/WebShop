package com.ptit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Products")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;
    String image;
    Double price;
    Integer quantity;
    @Temporal(TemporalType.DATE)
    @Column(name = "Createdate")
    Date createDate = new Date();
    Boolean available;

    @ManyToOne
    @JoinColumn(name = "Categoryid")
    Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    List<OrderDetail> orderDetails;
    
}