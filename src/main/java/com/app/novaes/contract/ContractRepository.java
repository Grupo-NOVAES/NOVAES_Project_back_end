package com.app.novaes.contract;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;


public interface ContractRepository extends JpaRepository<Contract, Long>{
	
	@Query("SELECT c FROM Contract c WHERE c.client.id = :id")
    List<Contract> findByClient(Long id);

	
	@Modifying
	@Transactional
	@Query("UPDATE Contract c SET c.concluded = true Where c.id = :contractId")
	void concludedContractById(Long contractId);
	
	@Modifying
	@Transactional
	@Query("UPDATE Contract c SET c.concluded = false Where c.id = :contractId")
	void desconcludedContractById(Long contractId);

}
