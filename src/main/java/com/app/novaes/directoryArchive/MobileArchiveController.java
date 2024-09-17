package com.app.novaes.directoryArchive;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.novaes.user.Role;
import com.app.novaes.user.User;
import com.app.novaes.user.UserRepository;

@RestController
@RequestMapping("/api/archive")
public class MobileArchiveController {

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private ArchiveRepository archiveRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private final DirectoryAndArchivesService directoryAndArchivesService;
    
    public MobileArchiveController(DirectoryAndArchivesService directoryAndArchivesService) {
    	this.directoryAndArchivesService=directoryAndArchivesService;
    }

    
    @GetMapping("/directory")
    public List<DirectoryDTO> getAllDirectories() throws Exception {
       return directoryAndArchivesService.getListDirectory();
    }
    
    @GetMapping("/directory/getRoot")
    public DirectoryDTO getDirectoryRoot(){
    	return directoryAndArchivesService.getDirectoryDtoById((long)1);
    }

    @GetMapping("/directory/{id}")
    public List<DirectoryDTO> getDirectoryById(@PathVariable Long id) {
        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        
        List<DirectoryDTO> listDirectory = new ArrayList<>();
        
        for(Directory directorys : directory.getSubDirectories()) {
        	listDirectory.add(DirectoryAndArchivesService.convertToDTORecursive(directorys));
        }
        
        for(DirectoryDTO dto : listDirectory ) {
        	dto.setSubDirectories(null);
        }
        return listDirectory;
    }
    
    @GetMapping("/directory/getNameDirectoryInRoot")
    public List<ReferencesDirectoryClientDTO> getNameByDirectoryInRoot(){
    	Directory root = directoryRepository.findById((long) 1)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
    	
    	List<ReferencesDirectoryClientDTO> listName = new ArrayList<>();
    	
    	for(Directory directory : root.getSubDirectories()) {
    		ReferencesDirectoryClientDTO references = new ReferencesDirectoryClientDTO();
    		references.setNameDirectory(directory.getName());
    		references.setIdReferencesDirectory(directory.getId());
    		listName.add(references);
    	}
    	
    	return listName;
    }
    
    @GetMapping("/directory/getNameOfDirectoryById/{id}")
    public ResponseEntity<Object> getNameOfDirectoryById(@PathVariable Long id) {
    	Directory directory = directoryRepository.findById(id).orElse(null);
    	if(directory == null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Directory not fount");
    	}
    	return ResponseEntity.ok(directory.getName());
    }

    @PostMapping("/directory")
    public Directory createDirectory(@RequestBody Directory directory) {
    	List<User> listUser = userRepository.findByRole(Role.ADMIN);
    	
    	for(User user: listUser) {
    		directory.addUserToListUserPermited(user);
    	}
    	
    	
        return directoryRepository.save(directory); 
    }

    @PutMapping("/directory/{id}")
    public Directory updateDirectory(@PathVariable Long id, @RequestBody Directory directoryDetails) {
        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        directory.setName(directoryDetails.getName());
        return directoryRepository.save(directory);
    }
    

    @DeleteMapping("/directory/{id}")
    public void deleteDirectory(@PathVariable Long id) {
    	Directory directory = directoryRepository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Directory not found"));
    	
    	if(directory.getName() == "root") {
    		new Exception("This directory it cannot be deleted");
    	}
        directoryRepository.deleteById(id);
    }

    
    
    @GetMapping
    public List<ArchiveDTO> getAllArchives() {
        List<Archive> archives = archiveRepository.findAll();
        List<ArchiveDTO> archiveDTOs = new ArrayList<>();
        for (Archive archive : archives) {
            archiveDTOs.add(DirectoryAndArchivesService.convertToDTO(archive));
        }
        return archiveDTOs;
    }

    @GetMapping("/getArchiveOfDirectory/{id_directory}")
    public List<ArchiveDTO> getAllArchiveOfDirectory(@PathVariable Long id_directory){
        return DirectoryAndArchivesService.convertToDTOArchives(archiveRepository.findByDirectory(id_directory));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getArchiveById(@PathVariable Long id) {
        Archive archive = archiveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archive not found"));
        
        ByteArrayResource resource = new ByteArrayResource(archive.getContent());
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+archive.getName()+"."+archive.getType().toLowerCase());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(archive.getContent().length)
                .body(resource);
    }
       
    @GetMapping("/getByDirectory/{id}")
    public List<ArchiveDTO> listAllArchiveWithoutContent(@PathVariable Long id) {
    	Directory directory = directoryRepository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Directory Not found"));
    	
    	List<Archive> ListArchives = directory.getArchives();
    	List<ArchiveDTO> ArchivesWithoutContent = new ArrayList<>();

    	for(Archive archive : ListArchives) {
    		ArchivesWithoutContent.add(DirectoryAndArchivesService.convertToDTO(archive));
    	}
    	return ArchivesWithoutContent;
    }
    
    @PostMapping("")
    public ArchiveDTO createArchive(@RequestParam("contentArchive") MultipartFile archiveData,
                                    @RequestParam("id_directory") Long directoryID) throws Exception {

        Directory directory = directoryRepository.findById(directoryID)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        
        byte[] content = archiveData.getBytes();
        
        Archive archive = new Archive();
        archive.setName(archiveData.getOriginalFilename());
        archive.setDirectory(directory);
        archive.setType(archiveData.getContentType());
        archive.setContent(content);
        archiveRepository.save(archive);
        
        return DirectoryAndArchivesService.convertToDTO(archive);
    }
    
    
    @PutMapping("/{id}")
    public Archive updateArchive(@PathVariable Long id, @RequestBody Archive archive) {
        Archive existingArchive = archiveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Archive not found"));

        existingArchive.setName(archive.getName());
        existingArchive.setType(archive.getType());
        existingArchive.setContent(archive.getContent());
        existingArchive.setDirectory(archive.getDirectory());

        return archiveRepository.save(existingArchive);
    }

    @DeleteMapping("/{id}")
    public void deleteArchive(@PathVariable Long id) {
        archiveRepository.deleteById(id);
    }
    

    
}
