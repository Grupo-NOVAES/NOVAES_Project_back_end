package com.app.novaes.stages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StageService {
	
	@Autowired
	private StagesRepository stagesRepository;
	
	protected List<Stage> getStagesByContractId(Long contract_id){
		return stagesRepository.findStagesByContract(contract_id);
	}

	protected void updateStage(Long id, String title, String description, Date dateHour) {
		Stage stage = stagesRepository.findById(id).orElseThrow(StageNotFoundException::new);
		if(title != null) {
			stage.setTitle(title);
		}if(description != null) {
			stage.setDescription(description);
		}if(dateHour != null) {
			stage.setDateHour(dateHour);
		}
		stagesRepository.save(stage);
	}

	protected void concludeStage(Long id) {
		Stage stage = stagesRepository.findById(id).orElseThrow(StageNotFoundException::new);
		if(stage.isStatus()) {
			stagesRepository.unConcludeStage(id);
		}else {
			stagesRepository.concludeStage(id);
		}
	}

	protected void deleteStage(Long id) {
		stagesRepository.deleteById(id);
	}

	protected Date String2Date(String dateHour) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    return format.parse(dateHour);
	}

	public Stage getStageById(Long id) {
		return stagesRepository.findById(id).orElseThrow(StageNotFoundException::new);
	}

}
