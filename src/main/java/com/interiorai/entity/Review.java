package com.interiorai.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="reviews",uniqueConstraints=@UniqueConstraint(columnNames={"author_id","professional_id"}))
public class Review {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="author_id") public Account author;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="professional_id") public ProfessionalProfile professional;
 public int rating;
 @Column(length=1000) public String comment;
 public Instant createdAt=Instant.now();
}
