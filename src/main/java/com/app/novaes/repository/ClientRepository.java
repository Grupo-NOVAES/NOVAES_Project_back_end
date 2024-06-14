package com.app.novaes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.novaes.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long>{

	Client findByLogin(String login);
}
