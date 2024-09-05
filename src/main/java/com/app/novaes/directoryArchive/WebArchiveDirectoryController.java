package com.app.novaes.directoryArchive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientService;
import com.app.novaes.user.User;
import com.app.novaes.user.UserService;

@Controller
public class WebArchiveDirectoryController {
	
	@Autowired
	private ArchiveRepository archiveRepository;
	private final DirectoryAndArchivesService directoryAndArchivesService;
	private final UserService userService;
	private final ClientService clientService;
    private static final Logger logger = LoggerFactory.getLogger(WebArchiveDirectoryController.class);

	public WebArchiveDirectoryController(DirectoryAndArchivesService directoryAndArchivesService,UserService userService,ClientService clientService) {
		this.directoryAndArchivesService=directoryAndArchivesService;
		this.userService=userService;
		this.clientService=clientService;
	}
	
	@GetMapping("/directory")
	public ModelAndView directoryListRoot() {
		ModelAndView modelAndView = new ModelAndView();
		
		User user = userService.getUserAuthInfo();
		modelAndView.addObject("imageProfile", userService.getProfilePhoto(user));
    	modelAndView.addObject("user", user);

        modelAndView.addObject("parentDirectoryId", (long)1);

    	if(userService.getTypeUser()) {
    		List<DirectoryDTO> nameParentDirectory = directoryAndArchivesService.getPathDirectoryById((long)1);
    		modelAndView.addObject("listNameParentDirectory" , nameParentDirectory);
    		
    		List<DirectoryDTO> listDirectory = directoryAndArchivesService.getListDirectory();
    			
    	    modelAndView.addObject("listDirectory" , listDirectory);
    	    modelAndView.addObject("listArchive" , new ArrayList<>());
    	        
    		modelAndView.setViewName("employee/directory");
    	}else {
    		
    		Client client = clientService.getClientAuthInfo();
    		List<DirectoryDTO> accessibleDirectories = directoryAndArchivesService.getAccessibleDirectories(client.getId());
    		List<DirectoryDTO> nameParentDirectory = directoryAndArchivesService.getPathDirectoryById(client.getReferences_directory());
    		List<ArchiveDTO> listArchive = directoryAndArchivesService.getListArchive(client.getReferences_directory());

    		nameParentDirectory.remove(0);
    		
    		modelAndView.addObject("listNameParentDirectory" , nameParentDirectory);
    		modelAndView.addObject("listDirectory", accessibleDirectories);
	        modelAndView.addObject("listArchive" , listArchive);

    		modelAndView.setViewName("client/directory");
    	}
    	
		
		return modelAndView;
	}
	
	@GetMapping("/directory/{id}")
	public ModelAndView directoryListRoot(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView();
			
		User user = userService.getUserAuthInfo();
		modelAndView.addObject("imageProfile", userService.getProfilePhoto(user));
    	modelAndView.addObject("user", user);
    	
    	
    	 modelAndView.addObject("parentDirectoryId", (long)id);

    		if(userService.getTypeUser()) {
    			List<DirectoryDTO> listDirectory = directoryAndArchivesService.getListSubDirectory(id);
    			List<DirectoryDTO> listNameParentDirectory = directoryAndArchivesService.getPathDirectoryById(id);
    			List<ArchiveDTO> listArchive = directoryAndArchivesService.getListArchive(id);
    			
    			
    			modelAndView.addObject("listNameParentDirectory" , listNameParentDirectory);
    	        modelAndView.addObject("listDirectory" , listDirectory);
    	        modelAndView.addObject("listArchive" , listArchive);
    	        
    			modelAndView.setViewName("employee/directory");
    		}else {
    			Client client = clientService.getClientAuthInfo();
    			
    			List<Long> listIdOfDirectoryPermited = new ArrayList<>();
    			
    			listIdOfDirectoryPermited.addAll(directoryAndArchivesService.getAllSubDirectoryIds(client.getReferences_directory()));
    			listIdOfDirectoryPermited.add(client.getReferences_directory());
    			
    			boolean permited = listIdOfDirectoryPermited.contains(id);
    			
                if(permited) {
                	List<DirectoryDTO> accessibleSubDirectories = directoryAndArchivesService.getSubDirectoryByParentDirectory(id);
                    List<DirectoryDTO> listNameParentDirectory = directoryAndArchivesService.getPathDirectoryById(id);
                    listNameParentDirectory.remove(0);
        			List<ArchiveDTO> listArchive = directoryAndArchivesService.getListArchive(id);

                    modelAndView.addObject("listNameParentDirectory" , listNameParentDirectory);
                    modelAndView.addObject("listDirectory", accessibleSubDirectories);
                    modelAndView.addObject("listArchive", listArchive);
                    
                    modelAndView.setViewName("client/directory");
                }else {
                	modelAndView.setViewName("ErrorPage");
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
	
	@GetMapping("/directory/download/{id}")
	public ResponseEntity<ByteArrayResource> downloadDirectoryById(@PathVariable Long id) {
	    Directory directory = directoryAndArchivesService.getDirectoryById(id);

	    byte[] zipBytes = directoryAndArchivesService.zipDirectory(directory);

	    ByteArrayResource resource = new ByteArrayResource(zipBytes);

	    HttpHeaders headers = new HttpHeaders();
	    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + directory.getName() + ".zip\"");

	    return ResponseEntity
	            .ok()
	            .headers(headers)
	            .contentLength(zipBytes.length)
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}



	
	@PostMapping("/directory")
	public String  addDirectory(@RequestParam("folderName") String folderName, @RequestParam("parentId") Long parentId) {
	    if(directoryAndArchivesService.addDirectory(folderName, parentId)) {
	    	return "redirect:/directory/"+parentId;
	    }else {
	    	return "redirect:/directory/"+parentId+"?messageError=409";
	    }
	    
	    
	}
	
	@PostMapping("/archive")
	public String addArchive(@RequestParam("file") MultipartFile file,@RequestParam("parentDirectoryId")Long parentDirectoryId) {
		directoryAndArchivesService.addFile(file,parentDirectoryId);
		return "redirect:/directory/"+parentDirectoryId;
	}
	
	@PostMapping("/directory/rename")
	public String renameDirectory(@RequestParam("directoryId") Long directoryId,
										@RequestParam("newNameFolder") String newNameFolder) {
		DirectoryDTO dto = directoryAndArchivesService.getDirectoryDtoById(directoryId);
		directoryAndArchivesService.renameFolder(directoryId , newNameFolder);
		return "redirect:/directory/"+dto.getParentDirectoryId();
	}
	
	@PostMapping("/archive/rename")
	public String renameArchive(@RequestParam("archiveId") Long archiveId,
							  @RequestParam("newNameArchive") String newNameArchive) {
		
		ArchiveDTO dto = directoryAndArchivesService.findArchiveById(archiveId);
		directoryAndArchivesService.renameArchive(archiveId , newNameArchive);
		return "redirect:/directory/"+dto.getParentDirectoryId();
}
	
	@PostMapping("/directory/delete/{directoryId}")
	public String deleteDirectory(@PathVariable Long directoryId) {
		DirectoryDTO dto = directoryAndArchivesService.getDirectoryDtoById(directoryId);
		directoryAndArchivesService.deleteDirectoryById(directoryId);
		return "redirect:/directory/"+dto.getParentDirectoryId();
	}
	
	@PostMapping("/archive/delete/{archiveId}")
	public String deleteArchive(@PathVariable Long archiveId) {
		ArchiveDTO dto = directoryAndArchivesService.findArchiveById(archiveId);
		directoryAndArchivesService.deleteArchiveById(archiveId);
		return "redirect:/directory/"+dto.getParentDirectoryId();
		
	}




	

}
