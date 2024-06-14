package com.app.novaes.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.novaes.model.Client;
import com.app.novaes.model.Directory;
import com.app.novaes.model.Employee;
import com.app.novaes.model.Role;
import com.app.novaes.model.User;
import com.app.novaes.repository.ClientRepository;
import com.app.novaes.repository.DirectoryRepository;
import com.app.novaes.repository.EmployeeRepository;
import com.app.novaes.repository.UserRepository;

@Component
public class DatabaseInit implements CommandLineRunner{
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private DirectoryRepository directoryRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		insertUserAdmin();
		inserRootDirectory();
		inserUsersToDirectoryes();
		
	}
	
	public void insertUserAdmin() {
		Client client = new Client();
        if(employeeRepository.findByLogin("admin@gmail.com") == null) {
        	Employee admin = new Employee();
        	admin.setName("Novaes");
        	admin.setLastname("ADM");
        	admin.setLogin("admin@gmail.com");
        	admin.setPassword(passwordEncoder.encode("123456"));
        	admin.setEnabled(true);
        	admin.setRole(Role.ADMIN);
        	admin.setOffice("Gerente");
        	admin.setAdmin(true);

            employeeRepository.save(admin);
        }
        if(employeeRepository.findByLogin("gerente@novaes.com") == null) {
        	Employee gerente = new Employee();
        	gerente.setName("Novaes");
        	gerente.setLastname("Employee");
        	gerente.setLogin("gerente@novaes.com");
        	gerente.setPassword(passwordEncoder.encode("123456"));
        	gerente.setEnabled(true);
        	gerente.setRole(Role.ADMIN);
        	gerente.setOffice("Funcionario");
        	gerente.setAdmin(true);

            employeeRepository.save(gerente);
        }
        if(employeeRepository.findByLogin("eng@novaes.com") == null) {
        	Employee employee = new Employee();
        	employee.setName("Novaes");
            employee.setLastname("Employee");
            employee.setLogin("eng@novaes.com");
            employee.setPassword(passwordEncoder.encode("123456"));
            employee.setEnabled(true);
            employee.setRole(Role.EMPLOYEE);
            employee.setOffice("Funcionario");
            employee.setAdmin(false);

            employeeRepository.save(employee);
        }
        if(clientRepository.findByLogin("client@novaes.com") == null) {
        	client.setName("Agúas");
        	client.setLastname("de Araçoiaba");
        	client.setLogin("client@novaes.com");
        	client.setPassword(passwordEncoder.encode("123456"));
        	client.setEnabled(true);
        	client.setRole(Role.USER);
        	client.setEntrerprise_name("Aguas de Araçoiaba");
        	client.setReferences_directory((long) 3);
 

            clientRepository.save(client);
        }
	}
	
	public void inserRootDirectory() {
		boolean ifRootNotExist = false;
		if(directoryRepository.findByName("root") == null) {
			Directory root = new Directory();
			root.setName("root");
			directoryRepository.save(root);
			ifRootNotExist=true;
		}
		if(ifRootNotExist) {
			Directory directory = new Directory();
			directory.setName("Jau");
			directory.setParentDirectory(directoryRepository.findByName("root"));
			directoryRepository.save(directory);
		}
		if(ifRootNotExist) {
			Directory directory = new Directory();
			directory.setName("Aguas de Aracoiaba");
			directory.setParentDirectory(directoryRepository.findByName("root"));
			directoryRepository.save(directory);
		}
		if(ifRootNotExist) {
			Directory directory = new Directory();
			directory.setName("Itu");
			directory.setParentDirectory(directoryRepository.findByName("root"));
			directoryRepository.save(directory);
		}
	}
	
	public void inserUsersToDirectoryes() {
		if(directoryRepository.findByName("root").getListUserPermited().isEmpty()) {
			Directory directory = directoryRepository.findByName("root");
			List<User> listUsers = userRepository.findByRole(Role.ADMIN);
			for(User user : listUsers) {
				directory.addUserToListUserPermited(user);
			}
			directoryRepository.save(directory);
		}
		Directory proutosEntregues = directoryRepository.findById((long)1).orElseThrow();
		if(directoryRepository.findById((long)2).orElseThrow(() -> new RuntimeException("Directory not exist")).getListUserPermited().isEmpty()) {
			
			Directory directory = directoryRepository.findById((long)2).orElseThrow(() -> new RuntimeException("Directory not exist"));
			List<User> listUsers = userRepository.findByRole(Role.ADMIN);
			for(User user : listUsers) {
				directory.addUserToListUserPermited(user);
			}
			directoryRepository.save(directory);
		}
		
		if(directoryRepository.findById((long)3).orElseThrow(() -> new RuntimeException("Directory not exist")).getListUserPermited().isEmpty()) {
			Directory directory = directoryRepository.findById((long)3).orElseThrow(() -> new RuntimeException("Directory not exist"));
			List<User> listUsers = userRepository.findByRole(Role.ADMIN);
			for(User user : listUsers) {
				directory.addUserToListUserPermited(user);
			}
			directoryRepository.save(directory);
		}
	}

}
