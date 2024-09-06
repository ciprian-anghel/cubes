package com.cubes.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cubes.config.Environment;
import com.cubes.domain.entity.Option;
import com.cubes.exception.AppException;
import com.cubes.utils.FileUtils;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage.BlobListOption;

@Service
public class StorageService {
	
	private static final Logger log = LoggerFactory.getLogger(StorageService.class);
	
	private static final String LOCAL_STORAGE_PATH = "localStorage/";
	private static final String FOLDER_PREFIX = "cubes";
	
	private final Bucket bucket;
	private final Environment environment;
	private final StorageProcessorService processor;
	private final FileUtils fileUtils;

	private List<Option> options = new ArrayList<>();
	
	@Autowired
    public StorageService(Bucket bucket, Environment environment, StorageProcessorService processor, FileUtils fileUtils) {
        this.bucket = bucket;
        this.environment = environment;
        this.processor = processor;
        this.fileUtils = fileUtils;
    }
    
    @PostConstruct
    private void postConstruct() {
    	initializeAssets();
    }
    
    public List<Option> getOptions() {
    	return Collections.unmodifiableList(options);
    }
    
    public void initializeAssets() {
    	log.info("Starting to initialize assets");
    	if (environment.isAlwaysDownloadAssets()) {
    		discardAndDownloadAssets();
    	} else {
    		loadAssetsFromCache();
    	}
    	options = processor.processOptions(Path.of(LOCAL_STORAGE_PATH, FOLDER_PREFIX));
    }
    
    private void discardAndDownloadAssets() {
    	fileUtils.discardCachedAssets(Paths.get(LOCAL_STORAGE_PATH).toFile());
    	downloadAssetsFromStorage();
    }
    
    private void loadAssetsFromCache() {
        log.info("Loading assets from cache...");
        if (Files.exists(Path.of(LOCAL_STORAGE_PATH))) {
            log.info("Cache found.");
        } else {
            log.info("Cache not found, downloading assets...");
            downloadAssetsFromStorage();
        }
    }
    
    private void downloadAssetsFromStorage() {
    	log.info("Downloading assets from remote storage");
    	bucket.list(BlobListOption.prefix(FOLDER_PREFIX))
    		  .streamAll()
    		  .filter(blob -> !fileUtils.isGcsDirectory(blob.getName()))
    		  .parallel()
    		  .forEach(this::downloadAsset);
    }

    private void downloadAsset(Blob blob) {
        String name = blob.getName();
        Path targetPath = Path.of(LOCAL_STORAGE_PATH, name);

        try {
        	fileUtils.createDirectoriesFromPath(targetPath.toString());
            blob.downloadTo(targetPath);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new AppException(
            		String.format("Failed to create directories or download file: %s", name), 
            		HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}