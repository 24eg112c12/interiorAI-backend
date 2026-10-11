package com.interiorai.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="professional_profiles",indexes={@Index(name="idx_prof_city",columnList="city"),@Index(name="idx_prof_profession",columnList="profession")})
public class ProfessionalProfile {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @OneToOne(fetch=FetchType.LAZY) @JoinColumn(unique=true) public Account account;
 @Column(nullable=false,length=130) public String name;
 @Column(nullable=false,length=70) public String profession;
 @Column(length=100) public String city;
 @Column(length=170) public String serviceArea;
 @Column(length=30) public String phone;
 public int experienceYears;
 @Column(length=2500) public String about;
 @Column(length=900) public String services;
 @Column(precision=13,scale=2) public BigDecimal startingPrice=BigDecimal.ZERO;
 @Column(length=500) public String photoUrl;
 public boolean verified=false;
 public boolean sample=false;
 public Instant createdAt=Instant.now();
}
