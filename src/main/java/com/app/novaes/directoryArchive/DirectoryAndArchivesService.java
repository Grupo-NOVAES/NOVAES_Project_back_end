package com.app.novaes.directoryArchive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.novaes.client.Client;
import com.app.novaes.client.ClientNotFoundException;
import com.app.novaes.client.ClientRepository;

@Service
public class DirectoryAndArchivesService {
	
	@Autowired
	private DirectoryRepository directoryRepository;
	
	@Autowired
	private ArchiveRepository archiveRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	
	protected static DirectoryDTO convertToDTORecursive(Directory directory) {
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
	
	protected static List<ArchiveDTO> convertToDTOArchives(List<Archive> archives) {
        List<ArchiveDTO> archiveDTOs = new ArrayList<>();
        for (Archive archive : archives) {
            archiveDTOs.add(convertToDTO(archive));
        }
        return archiveDTOs;
    }
	
	
	protected static ArchiveDTO convertToDTO(Archive archive) {
        ArchiveDTO dto = new ArchiveDTO();
        dto.setId(archive.getId());
        dto.setName(archive.getName());
        dto.setType(archive.getType());
        dto.setParentDirectoryId(archive.getDirectory() != null ? archive.getDirectory().getId() : null);
        return dto;
    }
	
	protected List<ArchiveDTO> getListArchive(Long id_directory){

        return archiveRepository.findArchivesByDirectoryId(id_directory);
	}
	
	
	protected List<DirectoryDTO> getListDirectory() {
        return directoryRepository.findSubDirectoriesOfRoot();
	}
	
	protected List<DirectoryDTO> getListSubDirectory(Long id){
        return directoryRepository.findSubDirectoriesByParentId(id);
	}
	
	protected List<String> getNameSubDirectoryByRoot() {
    	List<String> nameSubDirectoryByRoot = new ArrayList<>();
    	Directory root = directoryRepository.findById((long)1)
    				.orElseThrow(DirectoryNotFoundException :: new);
    	for(Directory directory : root.getSubDirectories()) {
    		nameSubDirectoryByRoot.add(directory.getName());
    	}
    	
    	return nameSubDirectoryByRoot;
    }
	
	protected List<DirectoryDTO> getAccessibleDirectories(long clientId) {
    	Client client = clientRepository.findById(clientId)
    			.orElseThrow(ClientNotFoundException :: new);
    	
    	Directory directoryFound = directoryRepository.findById(client.getReferences_directory())
    			.orElseThrow(DirectoryNotFoundException :: new);
  
    	List<DirectoryDTO> listDirectoryDTO = new ArrayList<>();
    	
    	for(Directory directory : directoryFound.getSubDirectories()) {
    		DirectoryDTO subDirectory = convertToDTORecursive(directory);
    		listDirectoryDTO.add(subDirectory);
    	}
    	listDirectoryDTO.remove(0);
    	return listDirectoryDTO;
    }
	
	protected List<DirectoryDTO> getSubDirectoryByParentDirectory(long directoryId){
    	Directory directoryFound = directoryRepository.findById(directoryId)
    			.orElseThrow(DirectoryNotFoundException :: new);
    	
    	List<DirectoryDTO> listDirectoryDTO = new ArrayList<>();
    	
    	for(Directory directory : directoryFound.getSubDirectories()) {
    		DirectoryDTO subDirectory = convertToDTORecursive(directory);
    		listDirectoryDTO.add(subDirectory);
    	}
    	return listDirectoryDTO;
    }
    
	protected List<Long> getAllSubDirectoryIds(Long directoryId) {
        List<Long> subDirectoryIds = new ArrayList<>();
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(DirectoryNotFoundException::new);
        
        collectSubDirectoryIds(directory, subDirectoryIds);
        return subDirectoryIds;
    }
    
	protected void collectSubDirectoryIds(Directory directory, List<Long> subDirectoryIds) {
        for (Directory subDirectory : directory.getSubDirectories()) {
            subDirectoryIds.add(subDirectory.getId());
            collectSubDirectoryIds(subDirectory, subDirectoryIds); 
        }
    }
    
	protected String getFileExtensionFromMimeType(String mimeType) {
		System.out.println("Tipo do Arquivo: "+mimeType);
        switch (mimeType) {
            case "application/pdf":
                return "pdf";
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/jfif":
            	return "jfif";         
            case "application/zip":
                return "zip";
            case "application/acad":
            case "application/x-autocad":
                return "dwg";
            case "application/dxf":
                return "dxf";
            case "application/sldprt":
                return "sldprt";
            case "application/sldasm":
                return "sldasm";
            case "application/slddrw":
                return "slddrw";
            case "application/x-tika-msoffice":
                return "rvt";
            case "application/octet-stream":
            	return "cpg";
            case "application/vnd.dbf":
            	return "dbf";
            case "text/html":
            	return "html";
            case "text/css":
            	return "css";
            case "application/javascript":
            	return "js";
            case "image/svg+xml":
            	return "svg";
            case "text/plain":
                return "txt";
            case "application/xml":
            	return "xml";
            default:
                return null; 
        }
    }
	
	private List<DirectoryDTO> buildPath(Directory directory) {
        List<DirectoryDTO> path = new ArrayList<>();
        while (directory != null) {
            DirectoryDTO dto = new DirectoryDTO();
            dto.setId_Directory(directory.getId());
            dto.setName(directory.getName());
            if (directory.getParentDirectory() != null) {
                dto.setParentDirectoryId(directory.getParentDirectory().getId());
                dto.setNameParentDirectory(directory.getParentDirectory().getName());
            }
            path.add(dto);
            directory = directory.getParentDirectory();
        }
        Collections.reverse(path);
        return path;
    }
    
	protected List<DirectoryDTO> getPathDirectoryById(Long id) {
        Directory directory = directoryRepository.findById(id)
                .orElseThrow(DirectoryNotFoundException::new);
        List<DirectoryDTO> listPath = buildPath(directory);
        
        return listPath;
    }

	protected void addDirectory(String folderName , Long parentId) {
		Directory parentDirectory = directoryRepository.findById(parentId).orElseThrow(DirectoryNotFoundException::new);
		Directory directory = new Directory();
		directory.setName(folderName);
		directory.setParentDirectory(parentDirectory);
		directoryRepository.save(directory);
	}

	public void renameFolder(Long directoryId, String newNameFolder) {
		directoryRepository.updateDirectoryName(directoryId , newNameFolder);
	}
}
