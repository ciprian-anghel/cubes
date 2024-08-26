package com.cubes.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cubes.config.Environment;
import com.cubes.dto.OptionDto;
import com.cubes.utils.FileUtils;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage.BlobListOption;

@Service
public class CubeStorageService {
	
	private static final String LOCAL_STORAGE_PATH = "localStorage/";
	private static final String FOLDER_PREFIX = "cubes";
	
	private final Bucket bucket;
	
	private OptionDto godFatherObject;

	@Autowired
    public CubeStorageService(Bucket bucket, Environment environment) {
        this.bucket = bucket;
    }
    
    @PostConstruct
    private void postConstruct() throws IOException {
    	init();
    }
    
    private void init() {
    	cleanPreviousDownloadedAssets();
    	
    	
    	downloadAllAssets();
    	//Create ObjectDto
    }
    
    private void cleanPreviousDownloadedAssets() {
    	FileUtils.deleteDirectory(Paths.get(LOCAL_STORAGE_PATH).toFile());
    }
    
    private void downloadAllAssets() {
    	bucket.list(BlobListOption.prefix(FOLDER_PREFIX))
    		  .streamAll()
    		  .filter(blob -> !FileUtils.isGcsDirectory(blob.getName()))
    		  .forEach(this::download);
    }

    void download(Blob blob) {
        String name = blob.getName();
        Path targetPath = Path.of(LOCAL_STORAGE_PATH, name);

        try {
            FileUtils.createDirectoriesFromPath(targetPath.toString());
            blob.downloadTo(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories or download file: " + name, e);
        }
    }
}
