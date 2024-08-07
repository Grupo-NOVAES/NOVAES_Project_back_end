package com.app.novaes.stages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StageService {
	
	@Autowired
	private StagesRepository stagesRepository;
	
	public List<Stages> getStagesByContractId(Long contract_id){
		return stagesRepository.findStagesByContract(contract_id);
	}

}
