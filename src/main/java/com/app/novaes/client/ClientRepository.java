package com.app.novaes.client;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long>{

	Client findByLogin(String login);
}
