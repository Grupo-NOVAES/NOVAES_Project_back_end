package com.app.novaes.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/user")
public class WebUserController {
	
	@GetMapping("/loginEmployee")
    public ModelAndView loginEmployee() {
    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("/loginEmployee.html");
    	return modelAndView;
    }
    
    
	/*
	 * @GetMapping("/perfil") public ModelAndView mostrarPerfil() { ModelAndView
	 * modelAndView = new ModelAndView();
	 * 
	 * Authentication authentication =
	 * SecurityContextHolder.getContext().getAuthentication(); UserDetails
	 * userDetails = (UserDetails) authentication.getPrincipal();
	 * 
	 * modelAndView.addObject("usuario", userDetails);
	 * modelAndView.setViewName("perfil.html"); return modelAndView; }
	 */
    
    @GetMapping("/home")
    public ModelAndView home() {
    	ModelAndView modelAndView = new ModelAndView();
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    	
    	for(GrantedAuthority authorities : userDetails.getAuthorities()) {
    		if(authorities == Role.ADMIN ||authorities == Role.EMPLOYEE) {
    			modelAndView.setViewName("/employee/homeEmployee.html");
    		}else {
    			modelAndView.setViewName("/client/homeClient.html");
    		}
    	}
    	
    	return modelAndView;
    }

}
