package com.interiorai.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="design_items")
public class DesignItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) public Design design;
 @Column(nullable=false,length=80) public String category;
 @Column(nullable=false,precision=10,scale=2) public BigDecimal quantity;
 @Column(nullable=false,precision=13,scale=2) public BigDecimal unitPrice;
 public BigDecimal total(){return quantity.multiply(unitPrice).setScale(2,java.math.RoundingMode.HALF_UP);}
}
