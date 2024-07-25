package com.app.novaes.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.novaes.directoryArchive.Archive;
import com.app.novaes.directoryArchive.ArchiveDTO;
import com.app.novaes.directoryArchive.Directory;
import com.app.novaes.directoryArchive.DirectoryDTO;
import com.app.novaes.directoryArchive.DirectoryNotFoundException;
import com.app.novaes.directoryArchive.DirectoryRepository;
import com.app.novaes.user.User;



@Service
public class DirectoryAndArchivesService {
	
	@Autowired
	private DirectoryRepository directoryRepository;
	
	public static void filterDirectories(Directory directory, User user, List<DirectoryDTO> directoryDtos) throws Exception {
        // Verifica se o usuário está na lista de usuários permitidos deste diretório
        if (directory.getListUserPermited().contains(user)) {
            directoryDtos.add(convertToDTORecursive(directory));
            
        }
        for (Directory subDirectory : directory.getSubDirectories()) {
            filterDirectories(subDirectory, user, directoryDtos);
        }
        
    }
	
	public static DirectoryDTO convertToDTORecursive(Directory directory) {
        DirectoryDTO dto = new DirectoryDTO();
        dto.setId_Directory(directory.getId());
        dto.setName(directory.getName());
        dto.setSubDirectories(new ArrayList<>());

        if (directory.getSubDirectories() != null && !directory.getSubDirectories().isEmpty()) {
            List<DirectoryDTO> subDirectoryDTOs = new ArrayList<>();
            for (Directory subDirectory : directory.getSubDirectories()) {
                subDirectoryDTOs.add(convertToDTORecursive(subDirectory));
            }
            dto.setSubDirectories(subDirectoryDTOs);
        }

        dto.setListArchives(convertToDTOArchives(directory.getArchives()));

        if (directory.getParentDirectory() != null) {
            dto.setParentDirectoryId(directory.getParentDirectory().getId());
        }
        
        dto.setNameParentDirectory(directory.getParentDirectory().getName());

        return dto;
    }
	
	public static List<ArchiveDTO> convertToDTOArchives(List<Archive> archives) {
        List<ArchiveDTO> archiveDTOs = new ArrayList<>();
        for (Archive archive : archives) {
            archiveDTOs.add(convertToDTO(archive));
        }
        return archiveDTOs;
    }
	
	
	public static ArchiveDTO convertToDTO(Archive archive) {
        ArchiveDTO dto = new ArchiveDTO();
        dto.setId(archive.getId());
        dto.setName(archive.getName());
        dto.setType(archive.getType());
        dto.setParentDirectoryId(archive.getDirectory() != null ? archive.getDirectory().getId() : null);
        return dto;
    }
	
	
	
	

}
