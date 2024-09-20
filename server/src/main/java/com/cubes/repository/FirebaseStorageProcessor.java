package com.cubes.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cubes.domain.entity.Option;

@Component
public class FirebaseStorageProcessor {
	
	private static final Logger log = LoggerFactory.getLogger(FirebaseStorageProcessor.class);
	
	private static final String ICON_PREFIX = "icon-";
	private static final String PNG_EXTENTION = ".png";
	
	private static int idCounter = 0;
	
    public List<Option> processOptions(Path cubesPath) {
    	log.info("Starting the creation of options.");

    	if (Files.notExists(cubesPath)) {
    		return List.of();
    	}
    	return mapFileToOption(cubesPath.toFile());
    }
    
    /**
     * In google cloud storage, the blob name is actually the entire path name.
     * 
     * It is important that the {@code name} input parameter is String in order to not loose "/".
     */
	public void createDirectoriesFromPath(String stringPath) throws IOException {
	    Path path = Path.of(stringPath);
	    if (isGcsDirectory(stringPath) || Files.notExists(path)) {
	        Files.createDirectories(path.getParent());
	    }
	}
	
	public boolean isAllowedFile(String path) {
		return !isGcsDirectory(path) && path.endsWith(PNG_EXTENTION);
	}
	
	/**
	 * Google Cloud Storage directories end with "/"
	 */
	private boolean isGcsDirectory(String path) {
		return path.endsWith("/");
	}
    
    private List<Option> mapFileToOption(File file) {
    	List<Option> options = new ArrayList<>();
    	for (File f : file.listFiles()) {
    		if (f.getName().startsWith(ICON_PREFIX)) {
    			continue;
    		}
    		
    		options.add(Option.builder()
    			.id(++idCounter)
				.path(f.getPath())
				.parentPath(f.getParent())
				.iconPath(getIconPath(f))
				.texturePath(f)
				.category(f.getParentFile().getName())
				.name(f.getName())
				.build());
    		
    		if (f.isDirectory()) {
    			options.addAll(mapFileToOption(f));
    		}
    	}
    	return options;
    }
    
    private String getIconPath(File path) {
    	if (path.getName().startsWith(ICON_PREFIX)) {
    		return path.getName();
    	}
    	
    	String parent = path.getParent();
    	String name = path.getName();
    	
    	if (path.isDirectory()) {
    		name = ICON_PREFIX + name + PNG_EXTENTION;
    	} else {
    		name = ICON_PREFIX + name;
    	}
    	return Path.of(parent, name).toString();
    }
    
}
