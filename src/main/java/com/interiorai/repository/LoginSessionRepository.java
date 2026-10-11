package com.interiorai.repository;
import com.interiorai.entity.LoginSession;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LoginSessionRepository extends JpaRepository<LoginSession,Long> { java.util.Optional<LoginSession> findByTokenHash(String tokenHash); java.util.List<LoginSession> findByAccountId(Long accountId); }
