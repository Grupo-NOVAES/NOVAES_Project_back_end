package com.app.novaes.model;



import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Client")
public class Client extends User {
    private String entrerprise_name;

    private Long references_directory;
}
