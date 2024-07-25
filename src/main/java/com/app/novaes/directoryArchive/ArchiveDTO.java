package com.app.novaes.directoryArchive;

import java.util.List;

import com.app.novaes.user.User;

import lombok.Data;

@Data
public class ArchiveDTO {
    private Long id;
    private String name;
    private String type;
    private Long parentDirectoryId;
    private List<User> listUserPermited;
    
    
}
