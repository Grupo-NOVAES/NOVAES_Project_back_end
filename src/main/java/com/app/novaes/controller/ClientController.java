package com.app.novaes.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.novaes.model.Client;
import com.app.novaes.model.Directory;
import com.app.novaes.model.Employee;
import com.app.novaes.model.Role;
import com.app.novaes.model.User;
import com.app.novaes.repository.ClientRepository;
import com.app.novaes.repository.DirectoryRepository;
import com.app.novaes.repository.EmployeeRepository;

@RestController
@RequestMapping("/client")
public class ClientController {
	
	@Autowired
    private ClientRepository clientRepository;
	
	@Autowired
	private DirectoryRepository directoryRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	

    @GetMapping
    public List<Client> getAllClientes() {
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Client getClienteById(@PathVariable Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    @PostMapping
    public ResponseEntity<Object> createCliente(@RequestBody Client cliente) {
        Client existClient = clientRepository.findByLogin(cliente.getLogin());
        if (existClient != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }
        Employee existEmployee = employeeRepository.findByLogin(cliente.getLogin());
        if (existEmployee != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }
        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        cliente.setRole(Role.USER);
        clientRepository.save(cliente);

        Directory root = directoryRepository.findById((long)1).orElseThrow(() -> new RuntimeException("Directory not found"));
        root.addUserToListUserPermited(cliente);
        directoryRepository.save(root);

        Directory produtosEntregues = directoryRepository.findById((long)2).orElseThrow(() -> new RuntimeException("Directory not found"));
        produtosEntregues.addUserToListUserPermited(cliente);
        directoryRepository.save(produtosEntregues);

        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCliente(@PathVariable Long id, @RequestBody Client clientDetails) {
    	Client cliente = clientRepository.findById(id).orElse(null);
    	
    	if(cliente == null) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Client not found");
    	}

        cliente.setName(clientDetails.getName());
        cliente.setLastname(clientDetails.getLastname());
        cliente.setLogin(clientDetails.getLogin());
        cliente.setEntrerprise_name(clientDetails.getEntrerprise_name());
        Directory directoryFound = directoryRepository.findById(clientDetails.getReferences_directory())
        		.orElse(null);
        
        if(directoryFound == null) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Directory not found");
        }
        
        cliente.setReferences_directory(directoryFound.getId());

        return ResponseEntity.ok(clientRepository.save(cliente));
    }

    @DeleteMapping("/{id}")
    public void deleteClienteFromAllDirectories(@PathVariable Long id) {
        List<Directory> directories = directoryRepository.findAll();

        for (Directory directory : directories) {
            if (removeClienteFromDirectory(directory, id)) {
                directoryRepository.save(directory);
            }
        }
        
        clientRepository.deleteById(id);
    }
    
    private boolean removeClienteFromDirectory(Directory directory, Long clienteId) {
        Optional<User> userOptional = directory.getListUserPermited().stream()
                                        .filter(user -> user.getId().equals(clienteId))
                                        .findFirst();
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            directory.getListUserPermited().remove(user);
            return true;
        }
        return false;
    }


}
