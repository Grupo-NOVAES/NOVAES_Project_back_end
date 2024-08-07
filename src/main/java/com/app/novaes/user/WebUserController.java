package com.app.novaes.user;

import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientDTO;
import com.app.novaes.client.ClientService;

@RestController
@RequestMapping("/user")
public class WebUserController {
	
	private UserService userService;
	
	private ClientService clientService;
	
	public WebUserController(UserService userService, ClientService clientService) {
		this.clientService=clientService;
		this.userService=userService;
	}
	
	@GetMapping
	public ModelAndView managersListScreenClient() {
		ModelAndView modelAndView = new ModelAndView();
		
		
		User user = userService.getUserAuthInfo();
    	modelAndView.addObject("user", user);
    
    	
    	if(userService.getTypeUser()) {
    		List<User> listContacts = userService.getAllUser();
    			
    	    modelAndView.addObject("listContacts", listContacts);
    	    	
    		modelAndView.setViewName("/employee/user.html");
    	}else {
    		List<User> listContacts = userService.getUserByRole(Role.ADMIN);
    			
    	    modelAndView.addObject("listContacts", listContacts);
    		modelAndView.setViewName("/client/managers.html");
    	}
    	
		
		return modelAndView;
	}
	
	@GetMapping("/profile")
    public ModelAndView profileScreen() {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getUserAuthInfo();
    	modelAndView.addObject("user", user);
    	
    	
    	modelAndView.addObject("imageProfile", userService.getProfilePhoto(user));
        boolean ifEmployee = userService.getTypeUser();
        if (ifEmployee) {           
            modelAndView.setViewName("/employee/profile.html");
        } else {
            Client client = clientService.getClientAuthInfo();
            ClientDTO clientDTO = clientService.convertAClientToClientDTO(client);

            modelAndView.addObject("user", clientDTO);
            modelAndView.setViewName("/client/profile.html");
        }

        return modelAndView;
    }
	
	@GetMapping("/profile/{id}")
    public ModelAndView profileScreen(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView();
        
        User userData = userService.getUserById(id);
        User user = userService.getUserAuthInfo();
        
        
    	modelAndView.addObject("user", user);
        modelAndView.addObject("user", userData);
    	modelAndView.addObject("imageProfile", userService.getProfilePhoto(userData));

        if(userService.getTypeUser()) {
        	
        }else {
        	
        }
        
       return modelAndView;
    }
	
	@PostMapping
	public void addUser() {
		
	}
	
	@PutMapping
	public void updateUser() {
		
	}
	
	@DeleteMapping
	public void deleteUser() {
		
	}
}
