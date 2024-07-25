package com.app.novaes.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByLogin(String login);
	
	List<User> findByRole(Role role);

}
