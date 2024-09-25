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
import com.cubes.domain.entity.Option.Builder;

/**
 * Class which creates the options metadata based on the locally stored assets.
 */
@Component
public class FirebaseStorageProcessor {
	
	private static final Logger log = LoggerFactory.getLogger(FirebaseStorageProcessor.class);
	
	public static final String PNG_EXTENTION = ".png";
	private static final String ICON_PREFIX = "icon-";
	private static final String BASE_COLOR_PREFIX = ICON_PREFIX + "base-color-";
	
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
    		if (isIcon(f)) {    			
    			continue;
    		}
    		
    		Builder builder = Option.builder()
				.id(++idCounter)
				.path(f.getPath())
				.parentPath(f.getParent())
				.iconPath(getIconPath(f))
				.texturePath(f)
				.category(f.getPath())
				.name(getName(f));
    		
				if (isIconWithEmbeddedColor(f)) {
					builder.color(parseColor(f.getName()));
				}
				options.add(builder.build());
    		
    		if (f.isDirectory()) {
    			options.addAll(mapFileToOption(f));
    		}
    	}
    	return options;
    }
    
    private boolean isIconWithEmbeddedColor(File file) {
    	return file.getName().startsWith(BASE_COLOR_PREFIX);
    }
    
    private boolean isIcon(File file) {
    	return file.getName().startsWith(ICON_PREFIX)
    			&& !isIconWithEmbeddedColor(file);
    }
    
    private int parseColor(String fileName) {
    	String hexValidator = "\\b([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\\b";
    	
    	String hexColor = fileName.replace(BASE_COLOR_PREFIX, "").replace(PNG_EXTENTION, "");
    	if (hexColor.matches(hexValidator)) {
    		return Integer.parseInt(hexColor, 16);
    	}
    	throw new IllegalArgumentException(String.format("A valid hex color could not be parsed from filename: %s", fileName));
    }
    
    private String getIconPath(File file) {
    	if (isIcon(file)) {
    		return file.getName();
    	}
    	
    	if (isIconWithEmbeddedColor(file)) {
    		return file.getPath();
    	}
    	
    	String parent = file.getParent();
    	String name = file.getName();
    	
    	if (file.isDirectory()) {
    		name = ICON_PREFIX + name + PNG_EXTENTION;
    	} else {
    		name = ICON_PREFIX + name;
    	}
    	return Path.of(parent, name).toString();
    }
    
    private String getName(File file) {
    	return file.getName().replace(PNG_EXTENTION, "");
    }
    
}
