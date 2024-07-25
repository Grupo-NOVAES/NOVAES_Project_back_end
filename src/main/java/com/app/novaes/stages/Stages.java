package com.app.novaes.stages;


import java.util.Date;

import com.app.novaes.contract.Contract;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class Stages {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    private String title;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateHour;
    
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

	
}
