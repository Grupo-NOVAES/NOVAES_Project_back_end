package com.app.novaes.model;

import java.util.List;

import com.app.novaes.dto.DirectoryDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Contract {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private boolean concluded;

    private String time;
    
    @ManyToOne
    private Client client;
    
    
    @ManyToMany
    private List<User> listUserVinculed;
    
    public void addUserToListUserVinculed(User user) {
    	listUserVinculed.add(user);
    }
    
    
    
}
