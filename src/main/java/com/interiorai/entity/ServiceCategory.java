package com.interiorai.entity;
import jakarta.persistence.*;
@Entity @Table(name="service_categories")
public class ServiceCategory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true,length=100) public String name;
}
