package com.app.novaes.client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
	
	private ClientRepository clientRepository;
	
	public ClientService(ClientRepository clientRepository) {
		this.clientRepository=clientRepository;
	}

	public Client getClientAuthInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Client client = (Client) authentication.getPrincipal();
		return client;
	}
	
	public Client getClientByLogin(String login) {
		return clientRepository.findByLogin(login);
	}
	
	public Client getClientById(Long id) {
		return clientRepository.findById(id).orElseThrow(ClientNotFoundException :: new);
	}
	
	public ClientDTO convertAClientToClientDTO(Client client) {
		ClientDTO clientDTO = new ClientDTO();
		clientDTO.setName(client.getName());
		clientDTO.setLastname(client.getLastname());
		clientDTO.setLogin(client.getLogin());
		clientDTO.setEnterpriseName(client.getEntrerprise_name());
		
		return clientDTO;
	}
}
