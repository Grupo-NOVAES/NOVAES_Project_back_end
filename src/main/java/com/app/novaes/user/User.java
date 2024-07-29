package com.app.novaes.user;

import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import lombok.Data;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static java.util.Collections.singleton;

import java.util.Base64;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public class User implements UserDetails{
	
	public User() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private String lastname;

    @Column(unique = true, nullable = false)
    private String login;

    private String password;
    
	@Column(length = 128000)
    @Lob
    private byte[] profilePhoto;
	
	private String phoneNumber;

    private boolean enabled = true;
    
    @Column(nullable = false)
    private Role role;
    
    public String getProfilePhotoBase64() {
        return this.profilePhoto != null ? Base64.getEncoder().encodeToString(this.profilePhoto) : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return singleton(role);
    }

	@Override
	public String getUsername() {
		var username = name + "" + lastname;
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}


}
