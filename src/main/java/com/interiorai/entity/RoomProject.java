package com.interiorai.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="room_projects",indexes=@Index(name="idx_project_owner",columnList="owner_id"))
public class RoomProject {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="owner_id") public Account owner;
 @Column(nullable=false,length=30) public String roomType;
 @Column(nullable=false,precision=10,scale=2) public BigDecimal length;
 @Column(nullable=false,precision=10,scale=2) public BigDecimal width;
 @Column(nullable=false,precision=10,scale=2) public BigDecimal height;
 @Column(nullable=false,length=10) public String unit="ft";
 public int doors; public int windows;
 @Column(length=1500) public String existingFurniture;
 @Column(length=1500) public String requirements;
 @Column(length=400) public String styles;
 @Column(length=400) public String colors;
 @Column(length=400) public String materials;
 @Column(length=1000) public String desiredFurniture;
 @Column(length=2000) public String instructions;
 @Column(nullable=false,precision=14,scale=2) public BigDecimal budget;
 public boolean includesMaterials=true; public boolean includesLabour=true;
 public boolean includesFurniture=true; public boolean includesInstallation=true;
 public Instant createdAt=Instant.now();
}
