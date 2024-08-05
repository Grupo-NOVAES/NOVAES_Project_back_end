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
import com.app.novaes.directoryArchive.DirectoryAndArchivesService;
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

	private UserDetails getUserAuthInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (UserDetails) authentication.getPrincipal();
	}
	
	

	
    
    
    
    private ClientDTO convertAClientToClientDTO(Client client) {
		ClientDTO clientDTO = new ClientDTO();
		clientDTO.setName(client.getName());
		clientDTO.setLastname(client.getLastname());
		clientDTO.setLogin(client.getLogin());
		clientDTO.setEnterpriseName(client.getEntrerprise_name());
		
		return clientDTO;
	}
    
    
    
    
    

    
    	
    
    	
	
	

}
