package com.app.novaes.contract;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ContractRepository extends JpaRepository<Contract, Long>{
	
	@Query("SELECT c FROM Contract c WHERE c.client.id = :id")
    List<Contract> findByClient(Long id);

}
