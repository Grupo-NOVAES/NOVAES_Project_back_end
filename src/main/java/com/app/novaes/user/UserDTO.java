package com.app.novaes.user;

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
