package com.interiorai.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="room_images")
public class RoomImage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(unique=true) public RoomProject project;
 @Column(nullable=false) public String storageName;
 @Column(nullable=false) public String originalName;
 @Column(nullable=false,length=50) public String contentType;
 public Instant createdAt=Instant.now();
}
