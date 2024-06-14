package com.app.novaes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.novaes.model.PersistentLogin;

public interface PersistentLoginRepository extends JpaRepository<PersistentLogin, String>{
	void deleteByUsername(String username);
}
