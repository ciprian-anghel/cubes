package com.cubes.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cubes.domain.entity.Option;

@Component
public class StorageProcessorService {
	
	private static final Logger log = LoggerFactory.getLogger(StorageProcessorService.class);
	
	private static final String ICON_PREFIX = "icon-";
	
    public List<Option> processOptions(Path cubesPath) {
    	log.info("Starting the creation of options.");

    	if (Files.notExists(cubesPath)) {
    		return List.of();
    	}
    	return mapFileToOption(cubesPath.toFile());
    }
    
    private List<Option> mapFileToOption(File file) {
    	List<Option> options = new ArrayList<>();
    	for (File f : file.listFiles()) {
    		if (f.getName().startsWith(ICON_PREFIX)) {
    			continue;
    		}
    		
    		options.add(Option.builder()
				.path(f.getPath())
				.parentPath(f.getParent())
				.iconPath(getIconPath(f))
				.texturePath(f)
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
    		name = ICON_PREFIX + name + ".png";
    	} else {
    		name = ICON_PREFIX + name;
    	}
    	return Path.of(parent, name).toString();
    }
	
}
