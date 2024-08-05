package com.app.novaes.directoryArchive;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArchiveRepository extends JpaRepository<Archive, Long>{
	
	@Query("SELECT a FROM Archive a WHERE a.directory.id = :idDirectory")
	List<Archive> findByDirectory(Long idDirectory);

	 @Query("SELECT new com.app.novaes.directoryArchive.ArchiveDTO(a.id, a.name, a.type, a.directory.id) FROM Archive a WHERE a.id = :id")
	 ArchiveDTO findArchiveDTOById(@Param("id") Long id);

	 @Query("SELECT new com.app.novaes.directoryArchive.ArchiveDTO(a.id, a.name, a.type, a.directory.id) FROM Archive a")
	 List<ArchiveDTO> findAllArchiveDTOs();
	 
	 @Query("SELECT new com.app.novaes.directoryArchive.ArchiveDTO(a.id, a.name, a.type, a.directory.id) FROM Archive a WHERE a.directory.id = :directoryId")
	 List<ArchiveDTO> findArchivesByDirectoryId(@Param("directoryId") Long directoryId);

}
