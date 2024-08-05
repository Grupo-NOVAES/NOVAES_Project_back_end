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
    
    public ArchiveDTO(Long id, String name, String type, Long parentDirectoryId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentDirectoryId = parentDirectoryId;
    }
    
    public ArchiveDTO() {};
}
