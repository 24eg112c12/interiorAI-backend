package com.interiorai.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="login_sessions",indexes=@Index(name="idx_session_hash",columnList="tokenHash",unique=true))
public class LoginSession {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true,length=64) public String tokenHash;
 @ManyToOne(fetch=FetchType.EAGER,optional=false) public Account account;
 @Column(nullable=false) public Instant expiresAt;
}
