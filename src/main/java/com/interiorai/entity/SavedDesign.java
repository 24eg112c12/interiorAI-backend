package com.interiorai.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="saved_designs",uniqueConstraints=@UniqueConstraint(columnNames={"account_id","design_id"}))
public class SavedDesign {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="account_id") public Account account;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="design_id") public Design design;
 public Instant createdAt=Instant.now();
}
