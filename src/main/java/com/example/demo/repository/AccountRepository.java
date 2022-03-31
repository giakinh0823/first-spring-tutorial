package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Account;

public interface AccountRepository  extends JpaRepository<Account, String>{
	List<Account> findByUsernameContaining(String username);
	Page<Account> findByUsernameContaining(String username, Pageable pageable);
}
