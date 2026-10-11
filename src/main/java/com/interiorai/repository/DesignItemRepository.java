package com.interiorai.repository;
import com.interiorai.entity.DesignItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DesignItemRepository extends JpaRepository<DesignItem,Long> { java.util.List<DesignItem> findByDesignIdOrderByIdAsc(Long designId); void deleteByDesignId(Long designId); }
