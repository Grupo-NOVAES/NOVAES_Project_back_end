package com.app.novaes;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.app.novaes.user.User;
import com.app.novaes.user.UserService;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private UserService userService;
	
	public GlobalExceptionHandler(UserService userService) {
		this.userService=userService;
	}
	
	@ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, Model model) {
		User user = userService.getUserAuthInfo();
        ModelAndView mav = new ModelAndView("ErrorPage");
        mav.addObject("user", user);
        mav.addObject("errorMessage", "Ocorreu um erro inesperado.");
        return mav;
    }

}
