package com.app.novaes.stages;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;


public interface StagesRepository extends JpaRepository<Stage, Long>{
	
	@Query("SELECT s FROM Stage s WHERE s.contract.id = :idContract")
	List<Stage> findStagesByContract(Long idContract);

	@Modifying
    @Transactional
	@Query("UPDATE Stage s SET s.status = true Where s.id = :id")
	void concludeStage(Long id);
	
	
	@Modifying
	@Transactional
	@Query("UPDATE Stage s SET s.status = false WHERE s.id = :id")
	void unConcludeStage(Long id);

}
