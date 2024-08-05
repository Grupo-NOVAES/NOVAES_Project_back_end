package com.app.novaes.directoryArchive;


import java.util.List;

import com.app.novaes.user.User;

import lombok.Data;

@Data
public class DirectoryDTO {
    private Long id_Directory;
    private String name;
    private List<DirectoryDTO> subDirectories;
    private List<ArchiveDTO> listArchives;
    private Long parentDirectoryId; 
    private String nameParentDirectory;
    
    public DirectoryDTO(Long id_Directory, String name, Long parentDirectoryId, String nameParentDirectory) {
        this.id_Directory = id_Directory;
        this.name = name;
        this.parentDirectoryId = parentDirectoryId;
        this.nameParentDirectory = nameParentDirectory;
    }
    
    DirectoryDTO() {};
}

