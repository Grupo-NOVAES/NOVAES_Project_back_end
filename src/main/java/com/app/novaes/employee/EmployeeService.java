package com.app.novaes.employee;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.app.novaes.user.User;

@Service
public class EmployeeService {
	
	private EmployeeRepository employeeRepository;
	
	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository=employeeRepository;
	}

	public Employee getEmployeeAuthInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Employee employee = (Employee) authentication.getPrincipal();
		return employee;
	}

	public void addUser(User user, String office) {
		Employee employee = new Employee();
		employee.setName(user.getName());
		employee.setLastname(user.getLastname());
		employee.setLogin(user.getLogin());
		employee.setPassword(user.getPassword());
		employee.setPhoneNumber(user.getPhoneNumber());
		employee.setRole(user.getRole());
		employee.setOffice(office);
		
		employeeRepository.save(employee);
	}

	public Employee getClientByLogin(String login) {
		return employeeRepository.findByLogin(login);
	}

	public void addUser(Employee employee) {
		employeeRepository.save(employee);
	}

	public Employee getEmployeeById(Long id) {
		return employeeRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);
	}
}
