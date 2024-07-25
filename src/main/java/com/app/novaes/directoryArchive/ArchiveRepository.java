package com.app.novaes.directoryArchive;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArchiveRepository extends JpaRepository<Archive, Long>{
	
	@Query("SELECT a FROM Archive a WHERE a.directory.id = :idDirectory")
	List<Archive> findByDirectory(Long idDirectory);


}
