package com.app.novaes.client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.novaes.user.User;

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
		clientDTO.setId(client.getId());
		clientDTO.setName(client.getName());
		clientDTO.setLastname(client.getLastname());
		clientDTO.setLogin(client.getLogin());
		clientDTO.setEnterpriseName(client.getEntrerprise_name());
		clientDTO.setRole(client.getRole());
		
		return clientDTO;
	}

	public void addUser(User user, String enterpriseName, Long references_directory) {
		Client client = new Client();
		client.setName(user.getName());
		client.setLastname(user.getLastname());
		client.setLogin(user.getLogin());
		client.setPassword(user.getPassword());
		client.setPhoneNumber(user.getPhoneNumber());
		client.setRole(user.getRole());
		client.setEntrerprise_name(enterpriseName);
		client.setReferences_directory(references_directory);
		System.out.println("diretorio relacionado: "+references_directory.toString());
		clientRepository.save(client);
		
	}

	public void addUser(Client client) {
		clientRepository.save(client);
		
	}
}
