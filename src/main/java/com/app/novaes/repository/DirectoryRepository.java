package com.app.novaes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.novaes.model.Directory;

public interface DirectoryRepository extends JpaRepository<Directory, Long>{
	
	Directory findByName(String name);

	
}
