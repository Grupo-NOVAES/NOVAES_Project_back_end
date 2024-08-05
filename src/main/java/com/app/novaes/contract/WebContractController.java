package com.app.novaes.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientRepository;

@RestController
@RequestMapping("/contract")
public class WebContractController {
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private ContractRepository contractRepository;

    @PostMapping
    public String addContract(@ModelAttribute Contract contract, RedirectAttributes redirectAttributes) {
        Client client = clientRepository.findByLogin(contract.getClient().getLogin());
        if (client == null) {
            redirectAttributes.addFlashAttribute("message", "Client not found");
            return "redirect:/contract";
        }
        contract.setClient(client);
        contractRepository.save(contract);
        redirectAttributes.addFlashAttribute("message", "Contrato adicionado com sucesso!");
        return "redirect:/contract";
    }
}
