package com.app.novaes.contract;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ContractService {
	
	private ContractRepository contractRepository;
	
	public ContractService(ContractRepository contractRepository) {
		this.contractRepository=contractRepository;
	}
	
	public List<Contract> getAllContract(){
		return contractRepository.findAll();
	}
	public Contract getContractById(Long id) {
		return contractRepository.findById(id).orElseThrow(ContractNotFoundException :: new);
	}
	public Contract findContractById(Long id) {
		return contractRepository.findById(id).orElseThrow(ContractNotFoundException :: new);
	}
	
	public void saveContract(Contract contract) {
        contractRepository.save(contract);
	}
	public void deleteContractById(Long id) {
		contractRepository.deleteById(id);
		
	}

}
