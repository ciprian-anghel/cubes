package com.cubes.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileUtils {
	
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
	
	public void deleteDirectory(File dirPath) {
        if (dirPath.exists()) {
            for (File file : dirPath.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                }
                file.delete();
            }
            dirPath.delete();
        }
    }
	
	/**
	 * Google Cloud Storage directories end with "/"
	 */
	public boolean isGcsDirectory(String path) {
		return path.endsWith("/");
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
	
    public void discardCachedAssets(File file) {
    	log.info("discardCachedAssets - remove cached assets");
    	deleteDirectory(file);
    }
	
}
