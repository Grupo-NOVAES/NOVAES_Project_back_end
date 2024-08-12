package com.app.novaes.directoryArchive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
	public List<ArchiveDTO> getListArchive(Long id_directory){
        return archiveRepository.findArchivesByDirectoryId(id_directory);
	}
	
	public List<DirectoryDTO> getListDirectory() {
        return directoryRepository.findSubDirectoriesOfRoot();
	}
	
	public List<DirectoryDTO> getListSubDirectory(Long id){
        return directoryRepository.findSubDirectoriesByParentId(id);
	}
	
	public Directory getDirectoryById(Long id) {
		return directoryRepository.findById(id).orElseThrow(DirectoryNotFoundException :: new);
	}
	
	public DirectoryDTO getDirectoryDtoById(Long id) {
		return directoryRepository.findDirectoryDTOById(id);
	}
	
	public List<String> getNameSubDirectoryByRoot() {
    	List<String> nameSubDirectoryByRoot = new ArrayList<>();
    	Directory root = directoryRepository.findById((long)1)
    				.orElseThrow(DirectoryNotFoundException :: new);
    	for(Directory directory : root.getSubDirectories()) {
    		nameSubDirectoryByRoot.add(directory.getName());
    	}
    	
    	return nameSubDirectoryByRoot;
    }
	
	public List<DirectoryDTO> getAccessibleDirectories(long clientId) {
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
	
	public List<DirectoryDTO> getSubDirectoryByParentDirectory(long directoryId){
    	Directory directoryFound = directoryRepository.findById(directoryId)
    			.orElseThrow(DirectoryNotFoundException :: new);
    	
    	List<DirectoryDTO> listDirectoryDTO = new ArrayList<>();
    	
    	for(Directory directory : directoryFound.getSubDirectories()) {
    		DirectoryDTO subDirectory = convertToDTORecursive(directory);
    		listDirectoryDTO.add(subDirectory);
    	}
    	return listDirectoryDTO;
    }
    
	public List<Long> getAllSubDirectoryIds(Long directoryId) {
        List<Long> subDirectoryIds = new ArrayList<>();
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(DirectoryNotFoundException::new);
        
        collectSubDirectoryIds(directory, subDirectoryIds);
        return subDirectoryIds;
    }
    
	public void collectSubDirectoryIds(Directory directory, List<Long> subDirectoryIds) {
        for (Directory subDirectory : directory.getSubDirectories()) {
            subDirectoryIds.add(subDirectory.getId());
            collectSubDirectoryIds(subDirectory, subDirectoryIds); 
        }
    }
    
	public String getFileExtensionFromMimeType(String mimeType) {
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
    
	public List<DirectoryDTO> getPathDirectoryById(Long id) {
        Directory directory = directoryRepository.findById(id)
                .orElseThrow(DirectoryNotFoundException::new);
        List<DirectoryDTO> listPath = buildPath(directory);
        
        return listPath;
    }
	
	public byte[] zipDirectory(Directory directory) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zipDirectoryRecursive(directory, "", zos);
        }
        return baos.toByteArray();
    }

    private void zipDirectoryRecursive(Directory directory, String parentPath, ZipOutputStream zos) throws IOException {
        String dirPath = parentPath + directory.getName() + "/";

        zos.putNextEntry(new ZipEntry(dirPath));
        zos.closeEntry();

        for (Archive archive : directory.getArchives()) {
            zos.putNextEntry(new ZipEntry(dirPath + archive.getName()));
            zos.write(archive.getContent());
            zos.closeEntry();
        }

        for (Directory subDir : directory.getSubDirectories()) {
            zipDirectoryRecursive(subDir, dirPath, zos);
        }
    }

	public void addDirectory(String folderName , Long parentId) {
		Directory parentDirectory = directoryRepository.findById(parentId).orElseThrow(DirectoryNotFoundException::new);
		Directory directory = new Directory();
		directory.setName(folderName);
		directory.setParentDirectory(parentDirectory);
		directoryRepository.save(directory);
	}
	
	public void RefresePageDirectory() {
		
	}

	public void renameFolder(Long directoryId, String newNameFolder) {
		directoryRepository.updateDirectoryName(directoryId , newNameFolder);
	}

	public void deleteDirectoryById(Long directoryId) {
		directoryRepository.deleteById(directoryId);
		
	}

	public void addFile(MultipartFile file, Long parentDirectoryId) {
		try {
            Archive archive = new Archive();
            archive.setName(file.getOriginalFilename());
            archive.setType(file.getContentType());
            archive.setContent(file.getBytes());

            Directory directory = directoryRepository.findById(parentDirectoryId)
                    .orElseThrow(DirectoryNotFoundException::new);
            archive.setDirectory(directory);

            archiveRepository.save(archive);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
}
