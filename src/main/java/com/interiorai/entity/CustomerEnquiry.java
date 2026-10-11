package com.interiorai.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="customer_enquiries",indexes={@Index(name="idx_enquiry_prof",columnList="professional_id"),@Index(name="idx_enquiry_customer",columnList="customer_id")})
public class CustomerEnquiry {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="customer_id") public Account customer;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="professional_id") public ProfessionalProfile professional;
 @ManyToOne(fetch=FetchType.LAZY) public Design design;
 @Column(nullable=false,length=2000) public String message;
 @Column(length=30) public String phone;
 @Column(nullable=false,length=25) public String status="NEW";
 public Instant createdAt=Instant.now();
}
