package com.app.novaes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tomcat.util.codec.binary.Base64;
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
import com.app.novaes.client.ClientDTO;
import com.app.novaes.client.ClientNotFoundException;
import com.app.novaes.client.ClientRepository;
import com.app.novaes.contract.Contract;
import com.app.novaes.contract.ContractNotFoundException;
import com.app.novaes.contract.ContractRepository;
import com.app.novaes.directoryArchive.Archive;
import com.app.novaes.directoryArchive.ArchiveDTO;
import com.app.novaes.directoryArchive.ArchiveRepository;
import com.app.novaes.directoryArchive.Directory;
import com.app.novaes.directoryArchive.DirectoryDTO;
import com.app.novaes.directoryArchive.DirectoryNotFoundException;
import com.app.novaes.directoryArchive.DirectoryRepository;
import com.app.novaes.stages.Stages;
import com.app.novaes.stages.StagesRepository;
import com.app.novaes.user.Role;
import com.app.novaes.user.User;
import com.app.novaes.user.UserNotFoundException;
import com.app.novaes.user.UserRepository;

@RestController
public class WebCentralNavigatorController {
	
	@Autowired
	private DirectoryRepository directoryRepository;
	
	@Autowired
	private ArchiveRepository archiveRepository;
	
	@Autowired
	private ContractRepository contractRepository;	
	
	@Autowired
	private StagesRepository stagesRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	private DirectoryAndArchivesService directoryAndArchivesService;
	
	@GetMapping("/contract")
	public ModelAndView contractScreenClient() {
		ModelAndView modelAndView = new ModelAndView();
		
    	UserDetails userDetails = getUserAuthInfo();
    	List<Contract> listContract = contractRepository.findAll();
    	
    	User user = getUserInfo();
    	modelAndView.addObject("user", user);
    	

    	modelAndView.addObject("listContract", listContract);
    	
    	for(GrantedAuthority authorities : userDetails.getAuthorities()) {
    		if(authorities == Role.ADMIN ||authorities == Role.EMPLOYEE) {
    			modelAndView.setViewName("/employee/contract.html");
    		}else {
    			modelAndView.setViewName("/client/contract.html");
    		}
    	}
		
		return modelAndView;
	}
	
	@GetMapping("/directory")
	public ModelAndView homeClient() {
		ModelAndView modelAndView = new ModelAndView();
		
		UserDetails userDetails = getUserAuthInfo();
		User user = getUserInfo();
    	modelAndView.addObject("user", user);
		
		
		
    	for(GrantedAuthority authorities : userDetails.getAuthorities()) {
    		if(authorities == Role.ADMIN ||authorities == Role.EMPLOYEE) {
    			List<DirectoryDTO> listDirectory = getListDirectory();
    			List<DirectoryDTO> nameParentDirectory = getPathDirectoryById((long)1);
    			
    			List<ArchiveDTO> listArchive = getListArchive();
    			
    			
    			modelAndView.addObject("listNameParentDirectory" , nameParentDirectory);
    	        modelAndView.addObject("listDirectory" , listDirectory);
    	        modelAndView.addObject("listArchive" , listArchive);
    	        
    			modelAndView.setViewName("/employee/directory.html");
    		}else {
    			Client client = (Client) userDetails;
    			List<DirectoryDTO> accessibleDirectories = getAccessibleDirectories(client.getId());
    			List<DirectoryDTO> nameParentDirectory = getPathDirectoryById(client.getReferences_directory());
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
    			List<DirectoryDTO> listDirectory = getListSubDirectory(id);
    			List<DirectoryDTO> listNameParentDirectory = getPathDirectoryById(id);
    			List<ArchiveDTO> listArchive = getListArchive();
    			
    			
    			modelAndView.addObject("listNameParentDirectory" , listNameParentDirectory);
    	        modelAndView.addObject("listDirectory" , listDirectory);
    	        modelAndView.addObject("listArchive" , listArchive);
    	        
    			modelAndView.setViewName("/employee/directory.html");
    		}else {
    			Client client = (Client) userDetails;
    			
    			List<Long> listIdOfDirectoryPermited = new ArrayList<>();
    			
    			listIdOfDirectoryPermited.addAll(getAllSubDirectoryIds(client.getReferences_directory()));
    			listIdOfDirectoryPermited.add(client.getReferences_directory());
    			
    			boolean permited = listIdOfDirectoryPermited.contains(id);
    			
                if(permited) {
                	List<DirectoryDTO> accessibleSubDirectories = getSubDirectoryByParentDirectory(id);
                    List<DirectoryDTO> listNameParentDirectory = getPathDirectoryById(id);
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

	    String fileExtension = getFileExtensionFromMimeType(mimeType);
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

	@GetMapping("/user")
	public ModelAndView managersListScreenClient() {
		ModelAndView modelAndView = new ModelAndView();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User user = getUserInfo();
    	modelAndView.addObject("user", user);
    	

    	for(GrantedAuthority authorities : userDetails.getAuthorities()) {
    		if(authorities == Role.ADMIN ||authorities == Role.EMPLOYEE) {
    			List<User> listContacts = userRepository.findAll();
    			
    	    	modelAndView.addObject("listContacts", listContacts);
    	    	
    			modelAndView.setViewName("/employee/user.html");
    		}else {
    			List<User> listContacts = userRepository.findByRole(Role.ADMIN);
    			
    	    	modelAndView.addObject("listContacts", listContacts);
    			modelAndView.setViewName("/client/managers.html");
    		}
    	}
		
		return modelAndView;
	}
	
	@GetMapping("/profile")
    public ModelAndView profileScreen() {
        ModelAndView modelAndView = new ModelAndView();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        User user = getUserInfo();
    	modelAndView.addObject("user", user);

        UserDetails userDetails = (UserDetails) principal;
        
        boolean ifEmployee = userDetails.getAuthorities().stream()
        		.anyMatch(authority -> authority.getAuthority()
        				.equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_EMPLOYEE"));

        if (ifEmployee) {           
            
            byte[] profilePhoto = user.getProfilePhoto();
            if (profilePhoto != null) {
                String base64Image = Base64.encodeBase64String(profilePhoto);
                modelAndView.addObject("imageProfile", base64Image);
            }
            modelAndView.setViewName("/employee/profile.html");
        } else {
            Client client = (Client) principal;
            ClientDTO clientDTO = convertAClientToClientDTO(client);

            modelAndView.addObject("user", clientDTO);

            modelAndView.setViewName("/client/profile.html");
        }

        return modelAndView;
    }
	
	@GetMapping("/profile/{id}")
    public ModelAndView profileScreen(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView();
        
        User userFound = userRepository.findById(id)
        		.orElseThrow(UserNotFoundException :: new);
        User user = getUserInfo();
    	modelAndView.addObject("user", user);
        
        modelAndView.addObject("user", userFound);
        
        byte[] profilePhoto = userFound.getProfilePhoto();
        if (profilePhoto != null) {
            String base64Image = Base64.encodeBase64String(profilePhoto);
            modelAndView.addObject("imageProfile", base64Image);
        }
        if(userFound.getRole() == Role.USER) {
        	
        }else {
        	
        }
        
       return modelAndView;
    }

	
	@GetMapping("/stage/{idContract}")
	public ModelAndView stagesScreenClient(@PathVariable Long idContract) {
		ModelAndView modelAndView = new ModelAndView();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    	
    	List<Stages> listStages = stagesRepository.findStagesByContract(idContract); 
    	User user = getUserInfo();
    	modelAndView.addObject("user", user);
    	
    	
    	for(GrantedAuthority authorities : userDetails.getAuthorities()) {
    		if(authorities == Role.ADMIN ||authorities == Role.EMPLOYEE) {
    			modelAndView.addObject("listStages", listStages);
    			
    			modelAndView.setViewName("/employee/stage.html");
    		}else {
    			Client client = (Client) authentication.getPrincipal();
    			Contract contractFound = contractRepository.findById(idContract)
    								.orElseThrow(ContractNotFoundException :: new);
    			
    			if(contractFound.getClass().equals(client)) {
    				modelAndView.addObject("listStages", listStages);
    				modelAndView.setViewName("/client/stage.html");
    			}
    			modelAndView.setViewName("/client/errorScreen.html");
    		}
    	}

		
		return modelAndView;
	}
	
	private User getUserInfo() {
		User user = (User) getUserAuthInfo();
		return user;
	}
	
	private List<ArchiveDTO> getListArchive(){
		Directory root = directoryRepository.findByName("jau");
        if (root == null) {
            throw new RuntimeException("Root directory not found");
           
        }
        List<ArchiveDTO> listArchive = new ArrayList<>();
        for(Archive archive : root.getArchives()) {
        	listArchive.add(directoryAndArchivesService.convertToDTO(archive));
        }
        return listArchive;
	}
	
	
	private List<DirectoryDTO> getListDirectory() {
		Directory root = directoryRepository.findByName("root");
        if (root == null) {
            throw new RuntimeException("Root directory not found");
           
        }
        List<DirectoryDTO> listDirectory = new ArrayList<>();
        for(Directory directory : root.getSubDirectories()) {
        	listDirectory.add(DirectoryAndArchivesService.convertToDTORecursive(directory));
        }
       
        
        return listDirectory;
	}
	
	private List<DirectoryDTO> getListSubDirectory(Long id){
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
	
	private UserDetails getUserAuthInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (UserDetails) authentication.getPrincipal();
	}
	
	private List<DirectoryDTO> getPathDirectoryById(Long id) {
        Directory directory = directoryRepository.findById(id)
                .orElseThrow(DirectoryNotFoundException::new);
        List<DirectoryDTO> listPath = buildPath(directory);
        
        return listPath;
    }

	private List<DirectoryDTO> buildPath(Directory directory) {
        List<DirectoryDTO> path = new ArrayList<>();
        while (directory != null) {
            DirectoryDTO dto = new DirectoryDTO();
            dto.setId_Directory(directory.getId());
            dto.setName(directory.getName());
            if (directory.getParentDirectory() != null) {
                dto.setParentDirectoryId(directory.getParentDirectory().getId());
                dto.setNameParentDirectory(directory.getParentDirectory().getName());
            }
            path.add(dto);
            directory = directory.getParentDirectory();
        }
        Collections.reverse(path);
        return path;
    }
    
    private List<String> getNameSubDirectoryByRoot() {
    	List<String> nameSubDirectoryByRoot = new ArrayList<>();
    	Directory root = directoryRepository.findById((long)1)
    				.orElseThrow(DirectoryNotFoundException :: new);
    	for(Directory directory : root.getSubDirectories()) {
    		nameSubDirectoryByRoot.add(directory.getName());
    	}
    	
    	return nameSubDirectoryByRoot;
    }
    
    private ClientDTO convertAClientToClientDTO(Client client) {
		ClientDTO clientDTO = new ClientDTO();
		clientDTO.setName(client.getName());
		clientDTO.setLastname(client.getLastname());
		clientDTO.setLogin(client.getLogin());
		clientDTO.setEnterpriseName(client.getEntrerprise_name());
		
		return clientDTO;
	}
    
    
    private List<DirectoryDTO> getAccessibleDirectories(long clientId) {
    	Client client = clientRepository.findById(clientId)
    			.orElseThrow(ClientNotFoundException :: new);
    	
    	Directory directoryFound = directoryRepository.findById(client.getReferences_directory())
    			.orElseThrow(DirectoryNotFoundException :: new);
  
    	List<DirectoryDTO> listDirectoryDTO = new ArrayList<>();
    	
    	for(Directory directory : directoryFound.getSubDirectories()) {
    		DirectoryDTO subDirectory = directoryAndArchivesService.convertToDTORecursive(directory);
    		listDirectoryDTO.add(subDirectory);
    	}
    	return listDirectoryDTO;
    }
    
    private List<DirectoryDTO> getSubDirectoryByParentDirectory(long directoryId){
    	Directory directoryFound = directoryRepository.findById(directoryId)
    			.orElseThrow(DirectoryNotFoundException :: new);
    	
    	List<DirectoryDTO> listDirectoryDTO = new ArrayList<>();
    	
    	for(Directory directory : directoryFound.getSubDirectories()) {
    		DirectoryDTO subDirectory = directoryAndArchivesService.convertToDTORecursive(directory);
    		listDirectoryDTO.add(subDirectory);
    	}
    	return listDirectoryDTO;
    }
    
    private List<Long> getAllSubDirectoryIds(Long directoryId) {
        List<Long> subDirectoryIds = new ArrayList<>();
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(DirectoryNotFoundException::new);
        
        collectSubDirectoryIds(directory, subDirectoryIds);
        return subDirectoryIds;
    }

    private void collectSubDirectoryIds(Directory directory, List<Long> subDirectoryIds) {
        for (Directory subDirectory : directory.getSubDirectories()) {
            subDirectoryIds.add(subDirectory.getId());
            collectSubDirectoryIds(subDirectory, subDirectoryIds); // Chamada recursiva
        }
    }
    	
    private String getFileExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "application/pdf":
                return "pdf";
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "text/plain":
                return "txt";
            case "application/zip":
                return "zip";
            case "application/acad":
            case "application/x-autocad":
                return "dwg";
            case "application/dxf":
                return "dxf";
            case "application/sldprt":
                return "sldprt";
            case "application/sldasm":
                return "sldasm";
            case "application/slddrw":
                return "slddrw";
            default:
                return null; 
        }
    }
    	
	
	

}
