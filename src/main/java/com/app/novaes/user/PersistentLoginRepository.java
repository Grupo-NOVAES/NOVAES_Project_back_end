package com.app.novaes.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistentLoginRepository extends JpaRepository<PersistentLogin, String>{
	void deleteByUsername(String username);
}
