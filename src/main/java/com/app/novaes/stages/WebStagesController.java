package com.app.novaes.stages;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientService;
import com.app.novaes.contract.Contract;
import com.app.novaes.contract.ContractService;
import com.app.novaes.user.User;
import com.app.novaes.user.UserService;


@Controller
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

	    List<Stage> listStages = stageService.getStagesByContractId(idContract); 
	    User user = userService.getUserAuthInfo();
	    modelAndView.addObject("user", user);

	    Contract contractFound = contractService.getContractById(idContract);
	    modelAndView.addObject("contract", contractFound);
	    contractService.concludedContract(idContract);

	    if(userService.getTypeUser()) {
	        modelAndView.addObject("listStages", listStages);
	        modelAndView.setViewName("employee/stage");
	    } else {
	        Client client = clientService.getClientAuthInfo();
	        
	        if(contractFound.getClient().equals(client)) {
	            modelAndView.addObject("listStages", listStages);
	            modelAndView.setViewName("client/stage");
	        } else {
	            modelAndView.setViewName("client/errorScreen");
	        }
	    }

	    return modelAndView;
	}

	
	@PostMapping
	public String addStages(@ModelAttribute Stage stages, RedirectAttributes redirectAttributes ) {
		
	    System.out.println("Data: " + stages.getDateHour());
	    Contract contract = contractService.getContractById(stages.getContract().getId());

	    if (contract == null) {
	        redirectAttributes.addFlashAttribute("message", "Contract not found");
	        return "redirect:/directory";
	    }
	    
	    stages.setContract(contract); 
	    
	    Client client = contract.getClient();
	    
	    if (client == null) {
	        redirectAttributes.addFlashAttribute("message", "Client not found");
	        return "redirect:/directory";
	    }
	    
	    contract.setClient(client);
	    stagesRepository.save(stages);

	    redirectAttributes.addFlashAttribute("message", "Etapa adicionada!");
	    return "redirect:/stages/"+contract.getId();
	}
	
	@PostMapping("/updateStage")
	public String updateStages(@RequestParam(value = "id")Long id,
							   @RequestParam(value = "title" , required = false)String title,
							   @RequestParam(value = "description", required = false)String description,
							   @RequestParam(value = "dateHour", required = false)String dateHour) throws ParseException {
		
		Date date = stageService.String2Date(dateHour);
		stageService.updateStage(id, title, description, date);
		
		return "redirect:/stages/"+stageService.getStageById(id).getContract().getId();
	}
	
	@PostMapping("/concludeStage/{id}")
	public String concludeStage(@PathVariable Long id) {
		Long idContract = stageService.getStageById(id).getContract().getId();
		stageService.concludeStage(id);
		
		return "redirect:/stages/"+idContract;
	}
	
	@PostMapping("/deleteStage/{id}")
	public String deleteStage(@PathVariable Long id) {
		Long idContract = stageService.getStageById(id).getContract().getId();
		stageService.deleteStage(id);
		return "redirect:/stages/"+idContract;
	}

	
}
