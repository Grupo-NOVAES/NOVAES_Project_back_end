package com.app.novaes.dto;

import com.app.novaes.model.Role;

import lombok.Data;

@Data
public class UserDTO {
	private Long id;
    private String name;
    private String lastname;
    private String login;
    private boolean enabled;
    private Role role;

}
