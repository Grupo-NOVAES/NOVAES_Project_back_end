package com.app.novaes.user;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientDTO;
import com.app.novaes.client.ClientService;
import com.app.novaes.directoryArchive.DirectoryAndArchivesService;
import com.app.novaes.employee.Employee;
import com.app.novaes.employee.EmployeeService;

@Controller
@RequestMapping("/user")
public class WebUserController {
	
	private UserService userService;
	
	private ClientService clientService;
	
	private EmployeeService employeeService;
	
	private DirectoryAndArchivesService directoryAndArchivesService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public WebUserController(UserService userService, ClientService clientService,DirectoryAndArchivesService directoryAndArchivesService,EmployeeService employeeService) {
		this.clientService=clientService;
		this.userService=userService;
		this.directoryAndArchivesService=directoryAndArchivesService;
		this.employeeService=employeeService;
	}
	
	@GetMapping
	public ModelAndView ListUsers() {
		ModelAndView modelAndView = new ModelAndView();
		
		
		User user = userService.getUserAuthInfo();
    	modelAndView.addObject("user", user);
    	modelAndView.addObject("imageProfile", userService.getProfilePhoto(user));
    
    	
    	if(userService.getTypeUser()) {
    		List<User> listContacts = userService.getAllUser();
    			
    	    modelAndView.addObject("listContacts", listContacts);
    	    	
    		modelAndView.setViewName("employee/user");
    	}else {
    		List<User> listContacts = userService.getUserByRole(Role.ADMIN);
    			
    	    modelAndView.addObject("listContacts", listContacts);
    		modelAndView.setViewName("client/managers");
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
            modelAndView.setViewName("employee/profile");
        } else {
            Client client = clientService.getClientAuthInfo();
            ClientDTO clientDTO = clientService.convertAClientToClientDTO(client);

            modelAndView.addObject("userData", clientDTO);
            modelAndView.setViewName("client/profile");
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
    		modelAndView.setViewName("ProfileVisit");
    		return modelAndView;
    	}
    }
	
	@GetMapping("/addUser")
	public ModelAndView addUser() {
		ModelAndView modelAndView = new ModelAndView();
		
		User user = userService.getUserAuthInfo();
		modelAndView.addObject("listDirectorys", directoryAndArchivesService.getListDirectory());
		
		if(userService.getTypeUser()) {
			modelAndView.addObject("user",user);
			
			modelAndView.setViewName("employee/addUser");
			
			return modelAndView;
		}else {
			modelAndView.setViewName("ErrorPage");
			return modelAndView;
		}
	}
	
	@PostMapping("/addUser")
	public String addUser(@RequestParam(value ="name")String name,
								@RequestParam(value ="lastname")String lastname,
								@RequestParam(value ="login")String login,
								@RequestParam(value ="password")String password,
								@RequestParam(value ="confirmPassword")String confirmPassword,
								@RequestParam(value ="phoneNumber")String phoneNumber,
								@RequestParam(value ="role")String role,
								@RequestParam(value ="office",required = false) String office,
								@RequestParam(value = "enterpriseName",required = false) String enterpriseName,
								@RequestParam(value = "referencesDirectory", required = false) Long references_directory) {
		
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
			if(user.getRole() == Role.USER) {
				clientService.addUser(user,enterpriseName,references_directory);
			}else {
				employeeService.addUser(user,office);
			}
		}
		
		return "redirect:/user";
	}
	
	@PostMapping("/editProfile")
	public String editProfile(
	        @RequestParam(value = "name", required = false) String name,
	        @RequestParam(value = "lastname", required = false) String lastname,
	        @RequestParam(value = "login", required = true) String login,
	        @RequestParam(value = "password", required = true) String password,
	        @RequestParam(value = "passwordConfirm", required = true) String passwordConfirm,
	        @RequestParam(value = "role", required = false) Role role) {
	    
	    String requestCode;

	    try {
	        if (!password.equals(passwordConfirm)) {
	            requestCode = "4001";
	            return "redirect:/user/profile?requestCode=" + requestCode;
	        }
	        if(password.length() < 6) {
	        	requestCode = "4002";
	        	return "redirect:/user/profile?requestCode=" + requestCode;
	        }

	        Long userId = userService.getUserAuthInfo().getId();

	        if (role == Role.USER) {
	            Client client = clientService.getClientById(userId);
	            userService.updateUserInfo(client, name, lastname, login, password, role);
	            clientService.addUser(client);
	        } else {
	            Employee employee = employeeService.getEmployeeById(userId);
	            userService.updateUserInfo(employee, name, lastname, login, password, role);
	            employeeService.addUser(employee);
	        }

	        requestCode = "200";
	    } catch (Exception e) {
	        requestCode = "500";
	    }

	    return "redirect:/user/profile?requestCode=" + requestCode;
	}
	
	@PutMapping("/editUser")
	public String editUser(
	        @RequestParam(value = "userId") Long id,
	        @RequestParam(value = "name", required = false) String name,
	        @RequestParam(value = "lastname", required = false) String lastname,
	        @RequestParam(value = "login", required = false) String login,
	        @RequestParam(value = "password", required = false) String password,
	        @RequestParam(value = "role", required = false) Role role,
	        @RequestParam(value = "phoneNumber", required = false) String phoneNumber) {
	    
	    String requestCode;

	    try {
	        if (role == null) {
	            requestCode = "4001"; // Role não pode ser nulo
	            return "redirect:/admin/users?requestCode=" + requestCode;
	        }

	        if (role == Role.USER) {
	            Client client = clientService.getClientById(id);
	            if (client == null) {
	                requestCode = "4002"; // Usuário não encontrado
	                return "redirect:/admin/users?requestCode=" + requestCode;
	            }
	            userService.updateUserInfo(client, name, lastname, login, password, role, phoneNumber);
	            clientService.addUser(client);
	        } else {
	            Employee employee = employeeService.getEmployeeById(id);
	            if (employee == null) {
	                requestCode = "4002"; // Usuário não encontrado
	                return "redirect:/admin/users?requestCode=" + requestCode;
	            }
	            userService.updateUserInfo(employee, name, lastname, login, password, role, phoneNumber);
	            employeeService.addUser(employee);
	        }

	        requestCode = "200"; // Sucesso
	    } catch (Exception e) {
	        requestCode = "500"; // Erro interno do servidor
	    }

	    return "redirect:/admin/users?requestCode=" + requestCode;
	}
	
	@PutMapping("/updateProfilePhoto")
	public void updateProfilePhoto(@RequestParam("userId") Long userId,
	                               @RequestParam("imageProfile") MultipartFile image) throws IOException {
	    User user = userService.getUserById(userId);
	    user.setProfilePhoto(image.getBytes());
	    userService.addUser(user);
	}


	
	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "redirect: /user";
	}
}
