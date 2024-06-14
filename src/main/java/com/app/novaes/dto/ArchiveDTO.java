package com.app.novaes.dto;

import java.util.List;

import com.app.novaes.model.User;

import lombok.Data;

@Data
public class ArchiveDTO {
    private Long id;
    private String name;
    private String type;
    private Long parentDirectoryId;
    private List<User> listUserPermited;
    
    
}
