package com.interiorai.repository;
import com.interiorai.entity.SavedDesign;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SavedDesignRepository extends JpaRepository<SavedDesign,Long> { boolean existsByAccountIdAndDesignId(Long accountId,Long designId); java.util.List<SavedDesign> findByAccountIdOrderByCreatedAtDesc(Long accountId); java.util.Optional<SavedDesign> findByAccountIdAndDesignId(Long accountId,Long designId); void deleteByDesignId(Long designId); }
