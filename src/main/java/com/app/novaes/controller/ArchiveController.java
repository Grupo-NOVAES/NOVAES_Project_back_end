package com.app.novaes.controller;

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

import com.app.novaes.dto.AddListPermissionDTO;
import com.app.novaes.dto.ArchiveDTO;
import com.app.novaes.dto.DirectoryDTO;
import com.app.novaes.dto.ReferencesDirectoryClientDTO;
import com.app.novaes.model.Archive;
import com.app.novaes.model.Directory;
import com.app.novaes.model.Role;
import com.app.novaes.model.User;
import com.app.novaes.repository.ArchiveRepository;
import com.app.novaes.repository.DirectoryRepository;
import com.app.novaes.repository.UserRepository;



@RestController
@RequestMapping("/archive")
public class ArchiveController {

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private ArchiveRepository archiveRepository;
    
    @Autowired
    private UserRepository userRepository;
    


    
    @GetMapping("/directory")
    public List<DirectoryDTO> getAllDirectories() throws Exception {

        
        Directory root = directoryRepository.findByName("root");
        if (root == null) {
            throw new RuntimeException("Root directory not found");
           
        }
        List<DirectoryDTO> listDirectory = new ArrayList<>();
        for(Directory directory : root.getSubDirectories()) {
        	listDirectory.add(convertToDTORecursive(directory));
        }
        
        for(DirectoryDTO dto : listDirectory ) {
        	dto.setListUserPermited(null);
        }
        return listDirectory;
    }
    
    @GetMapping("/directory/getRoot")
    public DirectoryDTO getDirectoryRoot(){
    	if(directoryRepository.findByName("root") == null) {
    		Directory directory = new Directory();
    		directory.setName("root");
    		directoryRepository.save(directory);
    	}
    	return convertToDTORecursive(directoryRepository.findByName("root"));
    }

    @GetMapping("/directory/{id}")
    public List<DirectoryDTO> getDirectoryById(@PathVariable Long id) {
        Directory directory = directoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        
        List<DirectoryDTO> listDirectory = new ArrayList<>();
        
        for(Directory directorys : directory.getSubDirectories()) {
        	listDirectory.add(convertToDTORecursive(directorys));
        }
        
        for(DirectoryDTO dto : listDirectory ) {
        	dto.setSubDirectories(null);
        	dto.setListUserPermited(null);
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
    
    @PutMapping("/directory/addUserToListOfUserPermited")
    public DirectoryDTO addListPermissionUser(@RequestBody AddListPermissionDTO dto) {
    	Directory directory = directoryRepository.findById((Long)dto.getId())
    			.orElseThrow(() -> new RuntimeException("Directory not Found"));
    	
    	User user = userRepository.findById((Long)dto.getIdUser())
    			.orElseThrow(() -> new RuntimeException("User not found"));
    	
    	directory.addUserToListUserPermited(user);
    	
    	return convertToDTORecursive(directoryRepository.save(directory));
    }

    @DeleteMapping("/directory/{id}")
    public void deleteDirectory(@PathVariable Long id) {
    	Directory directory = directoryRepository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Directory not found"));
    	
    	if(directory.getName() == "root" || directory.getName() == "Produtos Entregues" || directory.getName() == "Termos Contratuais") {
    		new Exception("This directory it cannot be deleted");
    	}
        directoryRepository.deleteById(id);
    }

    
    
    @GetMapping
    public List<ArchiveDTO> getAllArchives() {
        List<Archive> archives = archiveRepository.findAll();
        List<ArchiveDTO> archiveDTOs = new ArrayList<>();
        for (Archive archive : archives) {
            archiveDTOs.add(convertToDTO(archive));
        }
        return archiveDTOs;
    }

    @GetMapping("/getArchiveOfDirectory/{id_directory}")
    public List<ArchiveDTO> getAllArchiveOfDirectory(@PathVariable Long id_directory){
        return convertToDTOArchives(archiveRepository.findByDirectory(id_directory));
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
    		ArchivesWithoutContent.add(convertToDTO(archive));
    	}
    	return ArchivesWithoutContent;
    }
    
    @PostMapping
    public ArchiveDTO createArchive(@RequestParam("contentArchive") MultipartFile pdf, 
                                    @RequestParam String name, 
                                    @RequestParam Long directoryID, 
                                    @RequestParam String type) throws Exception {

        Directory directory = directoryRepository.findById(directoryID)
                .orElseThrow(() -> new RuntimeException("Directory not found"));
        
        byte[] content = pdf.getBytes();
        
        Archive archive = new Archive();
        archive.setName(name);
        archive.setDirectory(directory);
        archive.setType(type.toLowerCase());
        archive.setContent(content);
        archiveRepository.save(archive);
        
        return convertToDTO(archive);
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
    
    @PutMapping("/addUserToListOfUserPermited")
    public ArchiveDTO addListPermssionUserArchive(@RequestBody AddListPermissionDTO dto) {
    	Archive archive = archiveRepository.findById(dto.getId())
    			.orElseThrow(() -> new RuntimeException("Archive not found"));
    	User user = userRepository.findById(dto.getIdUser())
    			.orElseThrow(() -> new RuntimeException("User not found"));
    	archive.addUserToListPermited(user);
    	return convertToDTO(archiveRepository.save(archive));
    }

    @DeleteMapping("/{id}")
    public void deleteArchive(@PathVariable Long id) {
        archiveRepository.deleteById(id);
    }
    
    
    

    private DirectoryDTO convertToDTORecursive(Directory directory) {
        DirectoryDTO dto = new DirectoryDTO();
        dto.setId_Directory(directory.getId());
        dto.setName(directory.getName());
        dto.setSubDirectories(new ArrayList<>());

        if (directory.getSubDirectories() != null && !directory.getSubDirectories().isEmpty()) {
            List<DirectoryDTO> subDirectoryDTOs = new ArrayList<>();
            for (Directory subDirectory : directory.getSubDirectories()) {
                subDirectoryDTOs.add(convertToDTORecursive(subDirectory));
            }
            dto.setSubDirectories(subDirectoryDTOs);
        }

        dto.setListArchives(convertToDTOArchives(directory.getArchives()));

        if (directory.getParentDirectory() != null) {
            dto.setParentDirectoryId(directory.getParentDirectory().getId());
        }
        
        dto.setListUserPermited(directory.getListUserPermited());

        return dto;
    }

    private List<ArchiveDTO> convertToDTOArchives(List<Archive> archives) {
        List<ArchiveDTO> archiveDTOs = new ArrayList<>();
        for (Archive archive : archives) {
            archiveDTOs.add(convertToDTO(archive));
        }
        return archiveDTOs;
    }

    // Método auxiliar para converter Archive em ArchiveDTO
    private ArchiveDTO convertToDTO(Archive archive) {
        ArchiveDTO dto = new ArchiveDTO();
        dto.setId(archive.getId());
        dto.setName(archive.getName());
        dto.setType(archive.getType());
        dto.setParentDirectoryId(archive.getDirectory() != null ? archive.getDirectory().getId() : null);
        dto.setListUserPermited(archive.getListUserPermited());
        return dto;
    }
    
    private void filterDirectories(Directory directory, User user, List<DirectoryDTO> directoryDtos) throws Exception {
        // Verifica se o usuário está na lista de usuários permitidos deste diretório
        if (directory.getListUserPermited().contains(user)) {
            directoryDtos.add(convertToDTORecursive(directory));
            
        }
        for (Directory subDirectory : directory.getSubDirectories()) {
            filterDirectories(subDirectory, user, directoryDtos);
        }
        
    }
}
