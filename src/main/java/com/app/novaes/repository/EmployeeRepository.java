package com.app.novaes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.novaes.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long>{

	Employee findByLogin(String login);
}
