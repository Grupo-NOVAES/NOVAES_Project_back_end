package com.app.novaes.dto;
import java.util.List;

import com.app.novaes.model.Archive;
import com.app.novaes.model.User;

import lombok.Data;

@Data
public class DirectoryDTO {
    private Long id_Directory;
    private String name;
    private List<DirectoryDTO> subDirectories;
    private List<ArchiveDTO> listArchives;
    private Long parentDirectoryId; // Adicionando o campo para armazenar o ID do diretório pai
    private List<User> listUserPermited;
}

