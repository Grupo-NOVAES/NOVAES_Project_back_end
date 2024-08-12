package com.app.novaes.user;

import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.app.novaes.client.ClientRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;

	
	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
	};
	
	public boolean getTypeUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        boolean ifEmployee = userDetails.getAuthorities().stream()
        		.anyMatch(authority -> authority.getAuthority()
        				.equals("ROLE_ADMIN") || authority.getAuthority().equals("ROLE_EMPLOYEE"));
        
        return ifEmployee;
	}
	
	public User getUserAuthInfo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		
		return userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
	}
	
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(UserNotFoundException :: new);
	}
	
	public List<User> getAllUser(){
		return userRepository.findAll();
	}

	public List<User> getUserByRole(Role role){
		return userRepository.findByRole(role);
	}
	
	public boolean verifyIfAlreadyLoginExist(String login){
		List<String> listLogins = userRepository.getAllLogins();
		for(String loginFound : listLogins) {
			if(login == loginFound) {
				throw new ThisLoginAlreadyExistException();
			}
		}
		return false;
	}
	
	public String getProfilePhoto(User user){
		if(user.getProfilePhoto() != null) {
			return PhotoByte2Base64(user.getProfilePhoto());
		}else {
			return null;
		}
	}
	
	private String PhotoByte2Base64 (byte[] profilePhoto) {
		return Base64.encodeBase64String(profilePhoto);
	}

	public void addUser(User user) {
		userRepository.save(user);
	}
	
	public Role convertString2Role(String role) {
		if (role == null || role.isEmpty()) {
	        throw new IllegalArgumentException("Role string cannot be null or empty");
	    }

	    try {
	        return Role.valueOf(role.toUpperCase());
	    } catch (IllegalArgumentException e) {
	        throw new IllegalArgumentException("Invalid role: " + role, e);
	    }
	}

	public void deleteUser(Long userId) {
		userRepository.deleteById(userId);
	}
 
}
