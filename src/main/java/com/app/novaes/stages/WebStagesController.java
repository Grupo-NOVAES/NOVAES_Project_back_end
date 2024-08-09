package com.app.novaes.stages;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientService;
import com.app.novaes.contract.Contract;
import com.app.novaes.contract.ContractService;
import com.app.novaes.user.User;
import com.app.novaes.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/stages")
public class WebStagesController {

	private final UserService userService;
	private final ClientService clientService;
	private final StageService stageService;
	private final ContractService contractService;
	private final StagesRepository stagesRepository;
	
	public WebStagesController(UserService userService,ClientService clientService,StageService stageService,ContractService contractService,StagesRepository stagesRepository) {
		this.userService=userService;
		this.clientService=clientService;
		this.contractService=contractService;
		this.stageService=stageService;
		this.stagesRepository=stagesRepository;
	}
	
	@GetMapping("/{idContract}")
	public ModelAndView stagesScreenClient(@PathVariable Long idContract) {
	    ModelAndView modelAndView = new ModelAndView();

	    List<Stages> listStages = stageService.getStagesByContractId(idContract); 
	    User user = userService.getUserAuthInfo();
	    modelAndView.addObject("user", user);

	    Contract contractFound = contractService.getContractById(idContract);
	    modelAndView.addObject("contract", contractFound);

	    if(userService.getTypeUser()) {
	        modelAndView.addObject("listStages", listStages);
	        modelAndView.setViewName("/employee/stage.html");
	    } else {
	        Client client = clientService.getClientAuthInfo();
	        
	        if(contractFound.getClient().equals(client)) {
	            modelAndView.addObject("listStages", listStages);
	            modelAndView.setViewName("/client/stage.html");
	        } else {
	            modelAndView.setViewName("/client/errorScreen.html");
	        }
	    }

	    return modelAndView;
	}

	
	@PostMapping
	public ModelAndView addStages(@ModelAttribute Stages stages, RedirectAttributes redirectAttributes ) {
		
	    System.out.println("Data: " + stages.getDateHour());
	    Contract contract = contractService.getContractById(stages.getContract().getId());

	    if (contract == null) {
	        redirectAttributes.addFlashAttribute("message", "Contract not found");
	        return new ModelAndView("redirect:/stages");
	    }
	    
	    stages.setContract(contract); 
	    
	    Client client = contract.getClient();
	    
	    if (client == null) {
	        redirectAttributes.addFlashAttribute("message", "Client not found");
	        return new ModelAndView("redirect:/stages");
	    }
	    
	    contract.setClient(client);
	    stagesRepository.save(stages);

	    redirectAttributes.addFlashAttribute("message", "Etapa adicionada!");
	    return stagesScreenClient(contract.getId());
	}

	
}
