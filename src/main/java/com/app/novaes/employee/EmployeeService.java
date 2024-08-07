package com.app.novaes.employee;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class EmployeeService {

	public Employee getEmployeeAuthInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Employee employee = (Employee) authentication.getPrincipal();
		return employee;
	}
}
