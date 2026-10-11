package com.interiorai.repository;
import com.interiorai.entity.ProfessionalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProfessionalRepository extends JpaRepository<ProfessionalProfile,Long> { java.util.Optional<ProfessionalProfile> findByAccountId(Long accountId); java.util.List<ProfessionalProfile> findAllByOrderByIdDesc(); }
