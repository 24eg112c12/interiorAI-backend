package com.interiorai.entity;
import jakarta.persistence.*;
@Entity @Table(name="portfolio_images")
public class PortfolioImage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) public ProfessionalProfile professional;
 @Column(nullable=false,unique=true) public String storageName;
}
