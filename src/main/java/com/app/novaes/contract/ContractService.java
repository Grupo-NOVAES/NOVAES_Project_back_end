package com.app.novaes.contract;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.novaes.stages.Stage;
import com.app.novaes.stages.StageNotFoundException;
import com.app.novaes.stages.StagesRepository;

@Service
public class ContractService {
	
	private ContractRepository contractRepository;
	private StagesRepository stageRepository;
	
	public ContractService(ContractRepository contractRepository,StagesRepository stageRepository) {
		this.contractRepository=contractRepository;
		this.stageRepository=stageRepository;
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

	public List<Contract> getContractByClientId(Long clientId) {
		return contractRepository.findByClient(clientId);
	}

	public void concludedContract(Long idContract) {
		System.out.println("Rodando atualização");
		Contract contract = contractRepository.findById(idContract).orElseThrow(ContractNotFoundException::new);
			if(verifyIfConcluded(contract.getId())) {
				System.out.println("Concluindo contrato");
				contractRepository.concludedContractById(contract.getId());
			}else {
				System.out.println("descluindo contrato");
				contractRepository.desconcludedContractById(contract.getId());
			}
		
	}
	
	private boolean verifyIfConcluded(Long contractId) {
		List<Stage> listStages = stageRepository.findStagesByContract(contractId);
		
		for(Stage stage : listStages) {
			if(!stage.isStatus()) {
				System.out.println("algum estagio é falso");
				return false;
				
			}
			System.out.println(stage.toString());
		}
		return true;
	}

}
