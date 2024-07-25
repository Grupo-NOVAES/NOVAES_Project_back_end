package com.app.novaes.stages;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface StagesRepository extends JpaRepository<Stages, Long>{
	
	@Query("SELECT s FROM Stages s WHERE s.contract.id = :idContract")
	List<Stages> findStagesByContract(Long idContract);

}
