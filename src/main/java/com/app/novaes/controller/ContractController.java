package com.app.novaes.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.novaes.dto.AddListPermissionDTO;
import com.app.novaes.model.Client;
import com.app.novaes.model.Contract;
import com.app.novaes.model.Directory;
import com.app.novaes.model.Role;
import com.app.novaes.model.User;
import com.app.novaes.repository.ClientRepository;
import com.app.novaes.repository.ContractRepository;
import com.app.novaes.repository.DirectoryRepository;
import com.app.novaes.repository.UserRepository;

@RestController
@RequestMapping("/contract")
public class ContractController {

    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DirectoryRepository directoryRepository;

    @GetMapping
    public List<Contract> getAllContratos() {
    	List<Contract> listContract = contractRepository.findAll();
        return listContract;
    }
    
    @GetMapping("/getByIdClient/{id}")
    public List<Contract> GetContractByIdClient(@PathVariable Long id){
    	List<Contract> listContract = contractRepository.findByClient(id);
        return listContract;
    }

    @GetMapping("/{id}")
    public Contract getContratoById(@PathVariable Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));
        
       
        
        return contract;
    }

    @PostMapping
    public Contract createContrato(@RequestBody Contract contrato) throws Exception {
    	Client client = clientRepository.findByLogin(contrato.getClient().getLogin());
    	if(client == null) {
    		throw new Exception("Client not found");
    	}
    	contrato.setClient(client);
    	
        return contractRepository.save(contrato);
    }
    
    

    @PutMapping("/{id}")
    public Contract updateContrato(@PathVariable Long id, @RequestBody Contract contractDetails) {
    	Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));
    	contract.setTitle(contractDetails.getTitle());
    	contract.setConcluded(contractDetails.isConcluded());
    	contract.setTime(contractDetails.getTime());
    	
    	

        return contractRepository.save(contract);
    }
    
    @PutMapping("/addUserToList")
    public Contract addUserToListUserVinculedOfContract(@RequestBody AddListPermissionDTO dto) throws Exception {
    	Contract contract = contractRepository.findById(dto.getId())
    							.orElseThrow(() -> new RuntimeException("Contract not found"));
    	
    	User userToAdd = userRepository.findById(dto.getIdUser())
								.orElseThrow(() -> new RuntimeException("Contract not found"));
    	
    	for(User user : contract.getListUserVinculed()) {
    		if(user == userToAdd) {
    			throw new Exception("User already exist in the list");
    		}
    		if(userToAdd.getRole() == Role.USER && user.getRole() == Role.USER) {
    			throw new Exception("User already exist in the list");

    		}
    	}
    	contract.addUserToListUserVinculed(userToAdd);
    	
    	return contractRepository.save(contract);
    }

    @DeleteMapping("/{id}")
    public void deleteContrato(@PathVariable Long id) {
    	contractRepository.deleteById(id);
    }
    
    
}
