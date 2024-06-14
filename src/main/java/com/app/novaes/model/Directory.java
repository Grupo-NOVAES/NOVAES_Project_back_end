package com.app.novaes.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Directory {
	
	public Directory() {}
	
	public Directory(String name) {
		this.name=name;
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "parentDirectory", cascade = CascadeType.ALL)
    private List<Directory> subDirectories;
    
    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL)
    private List<Archive> archives;
    
    @ManyToOne
    @JoinColumn(name = "parent_directory_id")
    private Directory parentDirectory;
    
    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> listUserPermited = new ArrayList<>();
    
    public void addUserToListUserPermited(User user) {
    	listUserPermited.add(user);
    }
    
    

    
}
