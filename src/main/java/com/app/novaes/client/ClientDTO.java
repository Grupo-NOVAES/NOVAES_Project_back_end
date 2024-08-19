package com.app.novaes.client;

import com.app.novaes.directoryArchive.Directory;
import com.app.novaes.user.Role;

import lombok.Data;

@Data
public class ClientDTO {
	Long id;
	String name;
	String lastname;
	String login;
	String enterpriseName;
	Directory directoryPermited;
	Role role;
	
	

}
