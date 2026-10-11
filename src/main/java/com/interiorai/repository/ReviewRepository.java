package com.interiorai.repository;
import com.interiorai.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReviewRepository extends JpaRepository<Review,Long> { java.util.List<Review> findByProfessionalId(Long professionalId); java.util.Optional<Review> findByAuthorIdAndProfessionalId(Long authorId,Long professionalId); }
