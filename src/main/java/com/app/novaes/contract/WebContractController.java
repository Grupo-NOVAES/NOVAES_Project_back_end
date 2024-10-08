package com.app.novaes.contract;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientService;
import com.app.novaes.user.User;
import com.app.novaes.user.UserService;

@RestController
@RequestMapping("/contract")
public class WebContractController {
	
	private final UserService userService;
	private final ClientService clientService;
	private final ContractService contractService;
	
	public WebContractController(UserService userService, ClientService clientService, ContractService contractService) {
		this.clientService=clientService;
		this.contractService=contractService;
		this.userService=userService;
	}

	
	@GetMapping
	public ModelAndView contractScreenClient() {
	    ModelAndView modelAndView = new ModelAndView();
	    
	    User user = userService.getUserAuthInfo();
	    modelAndView.addObject("imageProfile", userService.getProfilePhoto(user));
	    modelAndView.addObject("user", user);
	    
		
	    if(userService.getTypeUser()) {
	    	List<Contract> listContract = contractService.getAllContract();
	    	
	    	modelAndView.addObject("listContract", listContract);
	        modelAndView.setViewName("employee/contract");
	    } else {
	    	List<Contract> listContract = contractService.getContractByClientId(user.getId());
	    			
	    	modelAndView.addObject("listContract", listContract);
	        modelAndView.setViewName("client/contract");
	    }

	    return modelAndView;
	}

    @PostMapping
    public ModelAndView addContract(@ModelAttribute Contract contract, RedirectAttributes redirectAttributes) {
        Client client = clientService.getClientByLogin(contract.getClient().getLogin());
        if (client == null) {
            redirectAttributes.addFlashAttribute("message", "Client not found");
            
        }
        contract.setClient(client);
        contractService.saveContract(contract);
        redirectAttributes.addFlashAttribute("message", "Contrato adicionado com sucesso!");
        return contractScreenClient();
    }
    
   
    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(
            @PathVariable Long id,
            @RequestBody ContractDto contractDto) {
    	
        Contract contract = contractService.findContractById(id);
        if (contract == null) {
            return ResponseEntity.notFound().build();
        }
        
        contract.setTitle(contractDto.getTitle());
        contractService.saveContract(contract);
        return ResponseEntity.ok(contract);
    }
    
    @DeleteMapping("/{id}")
    public void deleteContract(@PathVariable Long id) {
    	contractService.deleteContractById(id);
    }
}
