package com.app.novaes.directoryArchive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.tika.Tika;
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
		List<ArchiveDTO> listArchive = archiveRepository.findArchivesByDirectoryId(id_directory);
		for(ArchiveDTO dto : listArchive) {
			String typeArchive = getFileExtensionFromMimeType(dto.getType());
			dto.setNameArchive(dto.getName()+"."+typeArchive);
		}
        return listArchive;
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
    	
    	List<DirectoryDTO> listAcessibleDirectory = directoryRepository.findSubDirectoriesByParentId(client.getReferences_directory());
    	return listAcessibleDirectory;
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
            case "text/csv":
            	System.out.println("arquivo é planilha");
            	return "csv";
            case "text/plain":
        		System.out.println("arquivo é texto");
        		return "txt";
            case "application/zip":
            case "application/x-zip-compressed":
                return "zip";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
            case "application/x-tika-ooxml":
            	System.out.println("arquivo xlsx");
            	return "xlsx";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
            	return "docx";
 
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
            case"image/vnd.dxf; format=ascii":
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
            
            case "application/xml":
            	return "xml";
            default:
            	System.out.println("arquivo nulo");
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
	
	protected byte[] zipDirectory(Directory directory) {
	    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
	         ZipOutputStream zipOut = new ZipOutputStream(baos)) {

	        addFilesToZip(directory, zipOut, ""); // O caminho inicial é vazio para a raiz do zip

	        zipOut.finish();
	        return baos.toByteArray();
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to zip directory", e);
	    }
	}



	private void addFilesToZip(Directory directory, ZipOutputStream zipOut, String parentPath) throws IOException {
	    String currentPath = parentPath.isEmpty() ? directory.getName() : parentPath + "/" + directory.getName();

	    if (!parentPath.isEmpty()) {
	        zipOut.putNextEntry(new ZipEntry(currentPath + "/"));
	        zipOut.closeEntry();
	    }

	    for (Archive archive : directory.getArchives()) {
	        Tika tika = new Tika();
	        String mimeType = tika.detect(archive.getContent());
	        String fileExtension = getFileExtensionFromMimeType(mimeType);
	        
	        if (fileExtension == null) {
	            fileExtension = "bin"; 
	        }

	        String fileName = archive.getName() + "." + fileExtension;
	        ZipEntry zipEntry = new ZipEntry(currentPath + "/" + fileName);
	        zipOut.putNextEntry(zipEntry);
	        zipOut.write(archive.getContent());
	        zipOut.closeEntry();
	    }

	    for (Directory subDirectory : directory.getSubDirectories()) {
	        addFilesToZip(subDirectory, zipOut, currentPath);
	    }
	}



	public boolean addDirectory(String folderName , Long parentId) {
		if(verifyIfAFolderWithSameNameAlreadyExist(parentId,folderName)) {
			Directory parentDirectory = directoryRepository.findById(parentId).orElseThrow(DirectoryNotFoundException::new);
			Directory directory = new Directory();
			directory.setName(folderName);
			directory.setParentDirectory(parentDirectory);
			directoryRepository.save(directory);
			System.out.println("Adicionando Pasta");
			return true;
			
		}
		System.out.println("Nao adicionando Pasta");
		return false;
		
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
            archive.setName(getSomeNameFile(file.getOriginalFilename()));
            
            archive.setType(file.getContentType());
            System.out.println("tipo do arquivo que sera adicionado"+file.getContentType());
            archive.setContent(file.getBytes());

            Directory directory = directoryRepository.findById(parentDirectoryId)
                    .orElseThrow(DirectoryNotFoundException::new);
            archive.setDirectory(directory);

            archiveRepository.save(archive);
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

	private String getSomeNameFile(String originalFilename) {
		 if (originalFilename == null || originalFilename.isEmpty()) {
	            return originalFilename;
	     }
		 
		 int lastDotIndex = originalFilename.lastIndexOf('.');
		 
		 if (lastDotIndex <= 0) {
	            return originalFilename;
		 }
		 
		 String fileName = originalFilename.substring(0, lastDotIndex);
	        
	     String extension = originalFilename.substring(lastDotIndex);
	        
	     if (fileName.endsWith(extension)) {
	            return fileName.substring(0, fileName.length() - extension.length());
	     }
	        
	     return fileName;
	}

	public void renameArchive(Long archiveId, String newNameArchive) {
		archiveRepository.updateArchiveName(archiveId, newNameArchive);
		
	}

	public void deleteArchiveById(Long archiveId) {
		archiveRepository.deleteById(archiveId);
	}

	public ArchiveDTO findArchiveById(Long archiveId) {
		return archiveRepository.findArchiveDTOById(archiveId);
	}
	
	private boolean verifyIfAFolderWithSameNameAlreadyExist(Long parentDirectoryId,String newName) {
		List<DirectoryDTO> listDto = directoryRepository.findSubDirectoriesByParentId(parentDirectoryId);
		
		for(DirectoryDTO dto :listDto) {
			if(dto.getName().equals(newName)) {
				System.out.println("Alguma pasta contem o mesmo nome");
				return false;
			}
		}
		System.out.println("Nenhuma pasta contem o mesmo nome");
		return true;
	}


}
