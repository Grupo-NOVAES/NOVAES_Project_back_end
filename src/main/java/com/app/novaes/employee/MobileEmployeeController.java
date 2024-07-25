package com.app.novaes.employee;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientRepository;
import com.app.novaes.directoryArchive.Directory;
import com.app.novaes.directoryArchive.DirectoryRepository;
import com.app.novaes.user.Role;
import com.app.novaes.user.User;

@RestController
@RequestMapping("/api/employee")
public class MobileEmployeeController {
	
	 	@Autowired
	    private EmployeeRepository employeeRepository;
	 	
	 	@Autowired
	 	private DirectoryRepository directoryRepository;
	 	
	 	@Autowired
	 	private ClientRepository clientRepository;
	 
	 	@Autowired
		PasswordEncoder passwordEncoder;

	    @GetMapping()
	    public List<Employee> getAllFuncionarios() {
	        return employeeRepository.findAll();
	    }

	    @GetMapping("/{id}")
	    public Employee getFuncionarioById(@PathVariable Long id) {
	        return employeeRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
	    }

	    @PostMapping
	    public ResponseEntity<Object> createFuncionario(@RequestBody Employee employee) throws Exception {
	    	Employee existEmloyee = employeeRepository.findByLogin(employee.getLogin());
	    	
	    	if (existEmloyee != null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
	        }
	    	Client existClient = clientRepository.findByLogin(employee.getLogin());
	        if (existClient != null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
	        }
	    	
	    	if(employee.isAdmin()) {
	    		employee.setRole(Role.ADMIN);
	    		
	    	}else {
	    		employee.setRole(Role.EMPLOYEE);
	    		
	    	}
	    	employee.setPassword(passwordEncoder.encode(employee.getPassword()));
	    	employeeRepository.save(employee);
	    	
	    	Directory root = directoryRepository.findById((long)1).orElseThrow(() -> new RuntimeException("Directory not found"));
	    	root.addUserToListUserPermited(employee);
	    	directoryRepository.save(root);
	    	
	    	Directory produtosEntregues = directoryRepository.findById((long)2).orElseThrow(() -> new RuntimeException("Directory not found"));
	    	produtosEntregues.addUserToListUserPermited(employee);
	    	directoryRepository.save(produtosEntregues);
	    	
	    	Directory termosContratuais = directoryRepository.findById((long)3).orElseThrow(() -> new RuntimeException("Directory not found"));
	    	termosContratuais.addUserToListUserPermited(employee);
	    	directoryRepository.save(termosContratuais);
	    	
	        return ResponseEntity.ok(employee);
	    }

	    @GetMapping("/getAllContacts")
	    public List<Employee> getContacts(){
	    	List<Employee> contacts = new ArrayList<>();
	    	List<Employee> listEmployees = employeeRepository.findAll();
	    	for(Employee employee:listEmployees) {
	    		if(employee.getRole() == Role.ADMIN) {
	    			employee.setProfilePhoto(null);
	    			contacts.add(employee);
	    		}
	    	}
	    	
	    	return contacts;
	
	    }

	    @PutMapping("/{id}")
	    public Employee updateFuncionario(@PathVariable Long id, @RequestBody Employee employeeDetails) {
	    	Employee funcionario = employeeRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

	        funcionario.setName(employeeDetails.getName());
	        funcionario.setLastname(employeeDetails.getLastname());
	        funcionario.setLogin(employeeDetails.getLogin());


	        return employeeRepository.save(funcionario);
	    }

	    @DeleteMapping("/{id}")
	    public void deleteFuncionario(@PathVariable Long id) {
	        List<Directory> directories = directoryRepository.findAll();

	        for (Directory directory : directories) {
	            if (removeEmployeeFromDirectory(directory, id)) {
	                directoryRepository.save(directory);
	            }
	        }
	        
	    	employeeRepository.deleteById(id);
	    }
	    
	    private boolean removeEmployeeFromDirectory(Directory directory, Long employeeId) {
	        Optional<User> userOptional = directory.getListUserPermited().stream()
	                                        .filter(user -> user.getId().equals(employeeId))
	                                        .findFirst();
	        
	        if (userOptional.isPresent()) {
	            User user = userOptional.get();
	            
	            directory.getListUserPermited().remove(user);
	            return true;
	        }
	        return false;
	    }
	    
	    

	    

	
}
