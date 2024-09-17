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
		Client client = new Client();
        if(employeeRepository.findByLogin("admin@gmail.com") == null) {
        	Employee admin = new Employee();
        	admin.setName("Novaes");
        	admin.setLastname("ADM");
        	admin.setLogin("admin@gmail.com");
        	admin.setPassword(passwordEncoder.encode("123456"));
        	admin.setPhoneNumber("(16) 99999-9999");
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
        	gerente.setPhoneNumber("(16) 99999-9999");
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
            employee.setPhoneNumber("(16) 99999-9999");
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
        	client.setPhoneNumber("(16) 99999-9999");
        	client.setEnabled(true);
        	client.setRole(Role.USER);
        	client.setEntrerprise_name("Aguas de Araçoiaba");
        	client.setReferences_directory((long) 3);


            clientRepository.save(client);
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