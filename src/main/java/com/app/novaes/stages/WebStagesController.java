package com.app.novaes.stages;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientService;
import com.app.novaes.contract.Contract;
import com.app.novaes.contract.ContractService;
import com.app.novaes.user.User;
import com.app.novaes.user.UserService;

@RestController
@RequestMapping("/stages")
public class WebStagesController {

	private final UserService userService;
	private final ClientService clientService;
	private final StageService stageService;
	private final ContractService contractService;
	
	public WebStagesController(UserService userService,ClientService clientService,StageService stageService,ContractService contractService) {
		this.userService=userService;
		this.clientService=clientService;
		this.contractService=contractService;
		this.stageService=stageService;
	}
	
	@GetMapping("/{idContract}")
	public ModelAndView stagesScreenClient(@PathVariable Long idContract) {
		ModelAndView modelAndView = new ModelAndView();
    	
    	List<Stages> listStages = stageService.getStagesByContractId(idContract); 
    	User user = userService.getUserAuthInfo();
    	modelAndView.addObject("user", user);
    	
    	
    	if(userService.getTypeUser()) {
    		modelAndView.addObject("listStages", listStages);
    		modelAndView.setViewName("/employee/stage.html");
    	}else {
    		Client client = clientService.getClientAuthInfo();
    		Contract contractFound = contractService.getContractById(idContract);
    			
    		if(contractFound.getClient().getClass().equals(client)) {
    			modelAndView.addObject("listStages", listStages);
    			modelAndView.setViewName("/client/stage.html");
    		}
    		modelAndView.setViewName("/client/errorScreen.html");
    	}
    	

		
		return modelAndView;
	}
}
