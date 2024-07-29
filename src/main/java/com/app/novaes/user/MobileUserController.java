package com.app.novaes.user;

import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
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
import org.springframework.web.servlet.ModelAndView;

import com.app.novaes.employee.Employee;
import com.app.novaes.employee.EmployeeRepository;

@RestController
@RequestMapping("/api/user")
public class MobileUserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmployeeRepository employeeRepository;

	
	 	@GetMapping
	    public List<User> getAllUsuarios() {
	        return userRepository.findAll();
	    }

	    @GetMapping("/{id}")
	    public UserDTO getUsuarioById(@PathVariable Long id) {
	        User user =  userRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
	        UserDTO responseUser = new UserDTO();
	        responseUser.setId(user.getId());
	        responseUser.setName(user.getName());
	        responseUser.setLastname(user.getLastname());
	        responseUser.setRole(user.getRole());
	        responseUser.setLogin(user.getLogin());
	        responseUser.setPhoneNumber(user.getPhoneNumber());
	        
	        return responseUser;
	    }
	    
	    @GetMapping("/auth")
	    public ResponseEntity<?> isAuthenticated(@AuthenticationPrincipal User user) {
	        return ResponseEntity.noContent().build();
	    }
	    
	    @GetMapping("/currentUser")
	    public ResponseEntity<?> getCurrentUser() {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null && authentication.isAuthenticated()) {
	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	            return ResponseEntity.ok(userDetails);
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não está autenticado");
	        }
	    }
	    
	    @PostMapping("/create")
	    public User createUsuario(@RequestBody User usuario) {
	    	User existUser = userRepository.findByLogin(usuario.getLogin());
	    	
	    	if(existUser != null) {
	    		throw new Error("User already exist");
	    	}
	    	usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
	        return userRepository.save(usuario);
	    }
	    
	    @PutMapping("/addProfilePhoto/{id}")
	    public User addProfilePhoto(@PathVariable("id") long id,@RequestParam("profilePhoto") MultipartFile image)throws Exception {
	    	User user = userRepository.findById(id)
	    			.orElseThrow(() -> new RuntimeException("Employee not found"));
	    	
	    	
	    	
	    	byte[] content = image.getBytes();
	    	user.setProfilePhoto(content);
	    	return userRepository.save(user);
	    }
	    
	    @GetMapping("/getProfilePhoto/{id}")
	    public ResponseEntity<?> getProfilePhoto(@PathVariable("id") Long id)throws Exception {
	        User user = employeeRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Employee not found"));
	        if (user.getProfilePhoto() != null) {
	            String base64Image = Base64.getEncoder().encodeToString(user.getProfilePhoto());
	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .body(Collections.singletonMap("base64Image", base64Image));
	        } else {
	            throw new Exception("This profile doesn't have a Profile Photo");
	        }
	    }

	    @PutMapping("/{id}")
	    public User updateUsuario(@PathVariable Long id, @RequestBody User usuarioDetails) {
	    	User usuario = userRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

	        usuario.setName(usuarioDetails.getName());
	        usuario.setLastname(usuarioDetails.getLastname());
	        usuario.setPassword(usuarioDetails.getPassword());
	        usuario.setLogin(usuarioDetails.getLogin());

	        return userRepository.save(usuario);
	    }

	    @DeleteMapping("/{id}")
	    public void deleteUsuario(@PathVariable Long id) {
	    	userRepository.deleteById(id);
	    }
	    
	    
	    
	    
}
