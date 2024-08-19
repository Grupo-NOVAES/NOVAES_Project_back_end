package com.app.novaes.directoryArchive;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface DirectoryRepository extends JpaRepository<Directory, Long>{
	
	Directory findByName(String name);
	
	@Query("SELECT new com.app.novaes.directoryArchive.DirectoryDTO(d.id, d.name, d.parentDirectory.id, d.parentDirectory.name) FROM Directory d WHERE d.id = :id")
    DirectoryDTO findDirectoryDTOById(@Param("id") Long id);

    @Query("SELECT new com.app.novaes.directoryArchive.DirectoryDTO(d.id, d.name, d.parentDirectory.id, d.parentDirectory.name) FROM Directory d")
    List<DirectoryDTO> findAllDirectoryDTOs();
    
    @Query("SELECT new com.app.novaes.directoryArchive.DirectoryDTO(d.id, d.name, d.parentDirectory.id, d.parentDirectory.name) FROM Directory d WHERE d.parentDirectory.id = :parentId")
    List<DirectoryDTO> findSubDirectoriesByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT new com.app.novaes.directoryArchive.DirectoryDTO(d.id, d.name, d.parentDirectory.id, d.parentDirectory.name) FROM Directory d WHERE d.parentDirectory.id = 1")
    List<DirectoryDTO> findSubDirectoriesOfRoot();
    
    @Query("SELECT new com.app.novaes.directoryArchive.DirectoryDTO(d.id, d.name, d.parentDirectory.id, d.parentDirectory.name) FROM Directory d WHERE d.id = :directoryId OR d.parentDirectory.id = :directoryId")
    List<DirectoryDTO> findAllSubDirectoriesByClient(@Param("directoryId") Long directoryId);
    
    
    @Modifying
    @Transactional
    @Query("UPDATE Directory d SET d.name = :name WHERE d.id = :id")
    void updateDirectoryName(Long id, String name);


    

	
}
