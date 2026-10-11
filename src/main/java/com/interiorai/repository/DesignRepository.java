package com.interiorai.repository;
import com.interiorai.entity.Design;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DesignRepository extends JpaRepository<Design,Long> { java.util.List<Design> findByProjectIdOrderByIdAsc(Long projectId); }
