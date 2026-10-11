package com.interiorai.repository;
import com.interiorai.entity.CustomerEnquiry;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CustomerEnquiryRepository extends JpaRepository<CustomerEnquiry,Long> { java.util.List<CustomerEnquiry> findByCustomerIdOrderByCreatedAtDesc(Long customerId); java.util.List<CustomerEnquiry> findByProfessionalIdOrderByCreatedAtDesc(Long professionalId); }
