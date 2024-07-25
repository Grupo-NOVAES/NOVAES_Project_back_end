package com.app.novaes.user;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public enum Role implements GrantedAuthority{
	
	USER,
	EMPLOYEE,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }

}
