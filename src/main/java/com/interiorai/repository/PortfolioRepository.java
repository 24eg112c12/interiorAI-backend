package com.interiorai.repository;
import com.interiorai.entity.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PortfolioRepository extends JpaRepository<PortfolioImage,Long> { java.util.List<PortfolioImage> findByProfessionalId(Long professionalId); java.util.Optional<PortfolioImage> findByStorageName(String storageName); }
