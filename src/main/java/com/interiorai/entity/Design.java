package com.interiorai.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="designs",indexes=@Index(name="idx_design_project",columnList="project_id"))
public class Design {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="project_id") public RoomProject project;
 @Column(nullable=false,length=30) public String variant;
 @Column(length=1000) public String description;
 @Column(length=800) public String imageUrl;
 @Column(length=160) public String generatedFile;
 public boolean demo=true;
 public Instant createdAt=Instant.now();
}
