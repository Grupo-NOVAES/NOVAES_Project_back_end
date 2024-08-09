package com.app.novaes.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByLogin(String login);
	
	List<User> findByRole(Role role);

	
	@Query("SELECT u.login FROM User u")
	List<String> getAllLogins();

}
