package com.app.novaes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.novaes.model.User;

public interface UsersRepository extends JpaRepository<User, Long>{
	
	public User findByLogin(String login);

}
