package com.app.novaes.directoryArchive;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectoryRepository extends JpaRepository<Directory, Long>{
	
	Directory findByName(String name);

	
}
