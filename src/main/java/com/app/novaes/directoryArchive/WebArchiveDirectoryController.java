package com.app.novaes.directoryArchive;

import java.io.IOException;
import java.util.ArrayList;
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
    	modelAndView.addObject("user", user);
    	
		List<DirectoryDTO> nameParentDirectory = directoryAndArchivesService.getPathDirectoryById((long)1);
		modelAndView.addObject("listNameParentDirectory" , nameParentDirectory);


        modelAndView.addObject("parentDirectoryId", (long)1);

    	if(userService.getTypeUser()) {
    		List<DirectoryDTO> listDirectory = directoryAndArchivesService.getListDirectory();
    			
    	    modelAndView.addObject("listDirectory" , listDirectory);
    	    modelAndView.addObject("listArchive" , new ArrayList<>());
    	        
    		modelAndView.setViewName("/employee/directory.html");
    	}else {
    		Client client = clientService.getClientAuthInfo();
    		List<DirectoryDTO> accessibleDirectories = directoryAndArchivesService.getAccessibleDirectories(client.getId());
    			
    		modelAndView.addObject("listDirectory", accessibleDirectories);
    			
    		modelAndView.setViewName("/client/directory.html");
    	}
    	
		
		return modelAndView;
	}
	
	@GetMapping("/directory/{id}")
	public ModelAndView directoryListRoot(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView();
			
		User user = userService.getUserAuthInfo();
    	modelAndView.addObject("user", user);
    	
    	
    	 modelAndView.addObject("parentDirectoryId", (long)id);

    		if(userService.getTypeUser()) {
    			List<DirectoryDTO> listDirectory = directoryAndArchivesService.getListSubDirectory(id);
    			List<DirectoryDTO> listNameParentDirectory = directoryAndArchivesService.getPathDirectoryById(id);
    			List<ArchiveDTO> listArchive = directoryAndArchivesService.getListArchive(id);
    			
    			
    			modelAndView.addObject("listNameParentDirectory" , listNameParentDirectory);
    	        modelAndView.addObject("listDirectory" , listDirectory);
    	        modelAndView.addObject("listArchive" , listArchive);
    	        
    			modelAndView.setViewName("/employee/directory.html");
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
                    
                    modelAndView.addObject("listNameParentDirectory" , listNameParentDirectory);
                    modelAndView.addObject("listDirectory", accessibleSubDirectories);
                    
                    modelAndView.setViewName("/client/directory.html");
                }else {
                	modelAndView.setViewName("/ErrorPage.html");
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
    public ResponseEntity<ByteArrayResource> downloadDirectory(@PathVariable Long id) {
        try {
            logger.info("Buscando o diretório com ID: {}", id);
            Directory directory = directoryAndArchivesService.getDirectoryById(id);

            logger.info("Gerando arquivo zip para o diretório: {}", directory.getName());
            byte[] zipData = directoryAndArchivesService.zipDirectory(directory);

            ByteArrayResource resource = new ByteArrayResource(zipData);

            logger.info("Retornando arquivo zip com tamanho: {} bytes", zipData.length);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + directory.getName() + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipData.length)
                .body(resource);
        } catch (IOException e) {
            logger.error("Erro ao gerar o arquivo zip: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            logger.error("Erro inesperado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
	
	@PostMapping("/directory")
	public ModelAndView addDirectory(@RequestParam("folderName") String folderName, @RequestParam("parentId") Long parentId) {
	    directoryAndArchivesService.addDirectory(folderName, parentId);
	    return directoryListRoot();
	}
	
	@PostMapping("/archive")
	public ModelAndView addArchive(@RequestParam("file") MultipartFile file,@RequestParam("parentDirectoryId")Long parentDirectoryId) {
		directoryAndArchivesService.addFile(file,parentDirectoryId);
		return directoryListRoot();
	}
	
	@PostMapping("/directory/rename")
	public ModelAndView renameDirectory(@RequestParam("directoryId") Long directoryId,@RequestParam("newNameFolder") String newNameFolder) {
		directoryAndArchivesService.renameFolder(directoryId , newNameFolder);
		return directoryListRoot();
	}
	
	@DeleteMapping("/directory/delete/{directoryId}")
	public ModelAndView deleteDirectory(@PathVariable Long directoryId) {
		directoryAndArchivesService.deleteDirectoryById(directoryId);
		return directoryListRoot();
	}




	

}
