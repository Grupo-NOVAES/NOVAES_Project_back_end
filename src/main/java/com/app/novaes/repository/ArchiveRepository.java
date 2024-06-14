package com.app.novaes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.novaes.model.Archive;

public interface ArchiveRepository extends JpaRepository<Archive, Long>{
	
	@Query("SELECT a FROM Archive a WHERE a.directory.id = :idDirectory")
	List<Archive> findByDirectory(Long idDirectory);


}
