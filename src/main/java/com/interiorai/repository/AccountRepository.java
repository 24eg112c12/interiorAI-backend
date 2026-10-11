package com.interiorai.repository;
import com.interiorai.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AccountRepository extends JpaRepository<Account,Long> { java.util.Optional<Account> findByEmailIgnoreCase(String email); }
