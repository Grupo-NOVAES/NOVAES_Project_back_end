package com.app.novaes.contract;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientRepository;
import com.app.novaes.directoryArchive.DirectoryRepository;
import com.app.novaes.user.Role;
import com.app.novaes.user.User;
import com.app.novaes.user.UserRepository;



@RestController
@RequestMapping("/api/contract")
public class MobileContractController {

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
    public List<Contract> getContractByIdClient(@PathVariable Long id){
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
    public ResponseEntity<?> createContrato(@RequestBody Contract contrato) throws Exception {
    	Client client = clientRepository.findByLogin(contrato.getClient().getLogin());
    	if(client == null) {
    		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client not found");
    	}
    	contrato.setClient(client);
        return ResponseEntity.ok().body(contractRepository.save(contrato));
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

    @DeleteMapping("/{id}")
    public void deleteContrato(@PathVariable Long id) {
    	contractRepository.deleteById(id);
    }
    
    
}
