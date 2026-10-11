package com.interiorai.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="accounts", indexes=@Index(name="idx_accounts_email",columnList="email",unique=true))
public class Account {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=120) public String fullName;
 @Column(nullable=false,unique=true,length=190) public String email;
 @Column(nullable=false) public String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) public Role role=Role.USER;
 @Column(nullable=false,updatable=false) public Instant createdAt=Instant.now();
 @Column(nullable=false) public boolean enabled=true;
 public enum Role {USER, PROFESSIONAL, ADMIN}
}
