package com.app.novaes.user;

import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
		return user;
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
 
}
