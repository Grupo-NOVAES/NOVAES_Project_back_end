package com.app.novaes.directoryArchive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.app.novaes.client.Client;
import com.app.novaes.user.Role;
import com.app.novaes.user.User;

@RestController
public class WebArchiveDirectoryController {
	
	@Autowired
	private ArchiveRepository archiveRepository;
	
	private DirectoryAndArchivesService directoryAndArchivesService;
	
	public WebArchiveDirectoryController(DirectoryAndArchivesService directoryAndArchivesService) {
		this.directoryAndArchivesService=directoryAndArchivesService;
	}
	
	@GetMapping("/directory")
	public ModelAndView homeClient() {
		ModelAndView modelAndView = new ModelAndView();
		
		UserDetails userDetails = getUserAuthInfo();
		User user = getUserInfo();
    	modelAndView.addObject("user", user);
		
		
		
    	for(GrantedAuthority authorities : userDetails.getAuthorities()) {
    		if(authorities == Role.ADMIN ||authorities == Role.EMPLOYEE) {
    			List<DirectoryDTO> listDirectory = directoryAndArchivesService.getListDirectory();
    			List<DirectoryDTO> nameParentDirectory = directoryAndArchivesService.getPathDirectoryById((long)1);
    			List<ArchiveDTO> listArchive = new ArrayList<>();
    			
    			modelAndView.addObject("listNameParentDirectory" , nameParentDirectory);
    	        modelAndView.addObject("listDirectory" , listDirectory);
    	        modelAndView.addObject("listArchive" , listArchive);
    	        
    			modelAndView.setViewName("/employee/directory.html");
    		}else {
    			Client client = (Client) userDetails;
    			List<DirectoryDTO> accessibleDirectories = directoryAndArchivesService.getAccessibleDirectories(client.getId());
    			List<DirectoryDTO> nameParentDirectory = directoryAndArchivesService.getPathDirectoryById((long)1);
    			nameParentDirectory.remove(0);
    			
    			modelAndView.addObject("listNameParentDirectory" , nameParentDirectory);
    			modelAndView.addObject("listDirectory", accessibleDirectories);
    			
    			modelAndView.setViewName("/client/directory.html");
    		}
    	}
		
		return modelAndView;
	}
	
	@GetMapping("/directory/{id}")
	public ModelAndView homeClient(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView();
			
		UserDetails userDetails = getUserAuthInfo();
		User user = getUserInfo();
    	modelAndView.addObject("user", user);

    	for(GrantedAuthority authorities : userDetails.getAuthorities()) {
    		if(authorities == Role.ADMIN ||authorities == Role.EMPLOYEE) {
    			List<DirectoryDTO> listDirectory = directoryAndArchivesService.getListSubDirectory(id);
    			List<DirectoryDTO> listNameParentDirectory = directoryAndArchivesService.getPathDirectoryById(id);
    			List<ArchiveDTO> listArchive = directoryAndArchivesService.getListArchive(id);
    			
    			
    			modelAndView.addObject("listNameParentDirectory" , listNameParentDirectory);
    	        modelAndView.addObject("listDirectory" , listDirectory);
    	        modelAndView.addObject("listArchive" , listArchive);
    	        
    			modelAndView.setViewName("/employee/directory.html");
    		}else {
    			Client client = (Client) userDetails;
    			
    			List<Long> listIdOfDirectoryPermited = new ArrayList<>();
    			
    			listIdOfDirectoryPermited.addAll(directoryAndArchivesService.getAllSubDirectoryIds(client.getReferences_directory()));
    			listIdOfDirectoryPermited.add(client.getReferences_directory());
    			
    			boolean permited = listIdOfDirectoryPermited.contains(id);
    			
                if(permited) {
                	List<DirectoryDTO> accessibleSubDirectories = directoryAndArchivesService.getSubDirectoryByParentDirectory(id);
                    List<DirectoryDTO> listNameParentDirectory = directoryAndArchivesService.getPathDirectoryById(id);
                    listNameParentDirectory.remove(0);
                    
                    modelAndView.addObject("listNameParentDirectory" , listNameParentDirectory);
                    modelAndView.addObject("listDirectory", accessibleSubDirectories);
                    
                    modelAndView.setViewName("/client/directory.html");
                }else {
                	modelAndView.setViewName("/ErrorPage.html");
                }

    		}
    	}
		
		return modelAndView;

	}
	
	@GetMapping("/archive/download/{id}")
	public ResponseEntity<ByteArrayResource> downloadArchiveById(@PathVariable Long id) {
	    Archive archive = archiveRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Archive not found"));

	    ByteArrayResource resource = new ByteArrayResource(archive.getContent());

	    Tika tika = new Tika();
	    String mimeType = tika.detect(archive.getContent());

	    MediaType mediaType;
	    try {
	        mediaType = MediaType.parseMediaType(mimeType);
	    } catch (IllegalArgumentException e) {
	        mediaType = MediaType.APPLICATION_OCTET_STREAM;
	    }

	    String fileExtension = directoryAndArchivesService.getFileExtensionFromMimeType(mimeType);
	    if (fileExtension == null) {
	        fileExtension = "bin"; 
	    }

	    HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archive.getName() + "." + fileExtension + "\"");

	    return ResponseEntity
	            .ok()
	            .headers(headers)
	            .contentType(mediaType)
	            .contentLength(archive.getContent().length)
	            .body(resource);
	}
	
	private User getUserInfo() {
		User user = (User) getUserAuthInfo();
		return user;
	}

	private UserDetails getUserAuthInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (UserDetails) authentication.getPrincipal();
	}
	
	

}
