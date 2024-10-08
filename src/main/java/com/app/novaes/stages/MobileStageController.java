package com.app.novaes.stages;

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

import com.app.novaes.contract.Contract;
import com.app.novaes.contract.ContractRepository;


@RestController
@RequestMapping("/api/stages")
public class MobileStageController {

    @Autowired
    private StagesRepository stagesRepository;
    
    @Autowired
    private ContractRepository contractRepository;

    @GetMapping("/byContract/{id}")
    public List<Stage> getAllStages(@PathVariable Long id) {
    	List<Stage> listByContract = stagesRepository.findStagesByContract(id);
        return listByContract;
    }

    @GetMapping("/{id}")
    public Stage getStageById(@PathVariable Long id) {
    	
        return stagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estágio não encontrado"));
    }

    @PostMapping
    public Stage createStage(@RequestBody Stage stage) {
    	Contract contract = contractRepository.findById(stage.getContract().getId())
    							.orElseThrow(() -> new RuntimeException("Contract not found"));
    	stage.setContract(contract);
        return stagesRepository.save(stage);
    }

    @PutMapping("/{id}")
    public Stage updateStage(@PathVariable Long id, @RequestBody Stage stageDetails) {
        Stage stage = stagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estágio não encontrado"));

        stage.setTitle(stageDetails.getTitle());
        stage.setDescription(stageDetails.getDescription());
        stage.setDateHour(stageDetails.getDateHour());
        stage.setStatus(stageDetails.isStatus());
        stage.setContract(stageDetails.getContract());

        return stagesRepository.save(stage);
    }
    
    @PutMapping("/alterStatus/{id}")
    public Stage updateStatusStage(@PathVariable Long id) {
    	Stage stage = stagesRepository.findById(id).orElseThrow(() -> new RuntimeException("Stage not found"));
    	stage.setStatus(!stage.isStatus());
    	return stagesRepository.save(stage);
    }

    @DeleteMapping("/{id}")
    public void deleteStage(@PathVariable Long id) {
    	stagesRepository.deleteById(id);
    }
}

