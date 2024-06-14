package com.app.novaes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.novaes.model.Role;
import com.app.novaes.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByLogin(String login);
	
	List<User> findByRole(Role role);

}
