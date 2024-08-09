package com.app.novaes.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public WebUserController(UserService userService, ClientService clientService) {
		this.clientService=clientService;
		this.userService=userService;
	}
	
	@GetMapping
	public ModelAndView ListUsers() {
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
        modelAndView.addObject("userData", userData);
    	modelAndView.addObject("imageProfile", userService.getProfilePhoto(userData));
    	
    	System.out.println(user.getId().toString());
    	System.out.println(userData.getId().toString());
    	if(userData.equals(user)) {
    		return profileScreen();
    	}else {
    		modelAndView.setViewName("/ProfileVisit.html");
    		return modelAndView;
    	}
    }
	
	@GetMapping("/addUser")
	public ModelAndView addUser() {
		ModelAndView modelAndView = new ModelAndView();
		
		User user = userService.getUserAuthInfo();
		
		if(userService.getTypeUser()) {
			modelAndView.addObject("user",user);
			
			modelAndView.setViewName("/employee/addUser.html");
			
			return modelAndView;
		}else {
			modelAndView.setViewName("ErrorPage.html");
			return modelAndView;
		}
	}
	
	@PostMapping("/addUser")
	public ModelAndView addUser(@RequestParam("name")String name,
								@RequestParam("lastname")String lastname,
								@RequestParam("login")String login,
								@RequestParam("password")String password,
								@RequestParam("confirmPassword")String confirmPassword,
								@RequestParam("phoneNumber")String phoneNumber,
								@RequestParam("role")String role) {
		
		if(!(password.equals(confirmPassword))) {
			throw new PasswordNotEqualsException();
		}
		User user = new User();
		user.setName(name);
		user.setLastname(lastname);
		user.setLogin(login);
		user.setPhoneNumber(phoneNumber);
		user.setRole(userService.convertString2Role(role));
		user.setPassword(passwordEncoder.encode(password));
		
		if(!userService.verifyIfAlreadyLoginExist(login)) {
			userService.addUser(user);
		}
		
		return ListUsers();
	}
	
	@PutMapping
	public void updateUser() {
		
	}
	
	@DeleteMapping
	public void deleteUser() {
		
	}
}
