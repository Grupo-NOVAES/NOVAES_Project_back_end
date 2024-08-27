package com.app.novaes.util;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientRepository;
import com.app.novaes.directoryArchive.Directory;
import com.app.novaes.directoryArchive.DirectoryRepository;
import com.app.novaes.employee.Employee;
import com.app.novaes.employee.EmployeeRepository;
import com.app.novaes.user.Role;
import com.app.novaes.user.User;
import com.app.novaes.user.UserRepository;



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
	}
	
	public void insertUserAdmin() {
        if(employeeRepository.findByLogin("admin@gmail.com") == null) {
        	Employee admin = new Employee();
        	admin.setName("Admin");
        	admin.setLastname("Novaes");
        	admin.setLogin("adminNovaes@gmail.com");
        	admin.setPassword(passwordEncoder.encode("novaes2011"));
        	admin.setPhoneNumber("(16) 3419-0906");
        	admin.setEnabled(true);
        	admin.setRole(Role.ADMIN);
        	admin.setOffice("Gerente");
        	admin.setAdmin(true);

            employeeRepository.save(admin);
        }
	}
	
	public void inserRootDirectory() {
		if(directoryRepository.findByName("root") == null) {
			Directory root = new Directory();
			root.setName("root");
			directoryRepository.save(root);
		}
	}
}
