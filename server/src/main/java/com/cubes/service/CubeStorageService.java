package com.cubes.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger log = LoggerFactory.getLogger(CubeStorageService.class);
	
	private static final String LOCAL_STORAGE_PATH = "localStorage/";
	private static final String FOLDER_PREFIX = "cubes";
	
	private final Bucket bucket;
	private final Environment environment;
	private final OptionDto optionRoot = new OptionDto();

	@Autowired
    public CubeStorageService(Bucket bucket, Environment environment) {
        this.bucket = bucket;
        this.environment = environment;
    }
    
    @PostConstruct
    private void postConstruct() {
    	initializeAssets();
    }
    
    private void initializeAssets() {
    	log.info("initializeAssets - starting to initialize assets");
    	if (environment.isAlwaysDownloadAssets()) {
    		discardAndDownloadAssets();
        	return;
    	} else {
    		downdloadAssetsIfNotInCache();
    	}
    	initializeOptionRoot();
    }
    
    private void discardAndDownloadAssets() {
    	discardCachedAssets();
    	downloadAllAssets();
    }
    
    private void downdloadAssetsIfNotInCache() {
    	Path localStorage = Path.of(LOCAL_STORAGE_PATH);
    	if (Files.exists(localStorage)) {
    		log.info("downdloadAssetsIfNotInCache - assets cache found, assets will be used from cache");
    		return;
    	}
    	log.info("downdloadAssetsIfNotInCache - assets cache not found, assets will be downloaded from remote storage");
		downloadAllAssets();
    }
    
    private void discardCachedAssets() {
    	log.info("discardCachedAssets - remove cached assets");
    	FileUtils.deleteDirectory(Paths.get(LOCAL_STORAGE_PATH).toFile());
    }
    
    private void downloadAllAssets() {
    	log.info("downloadAllAssets - downloading assets from remote storage");
    	bucket.list(BlobListOption.prefix(FOLDER_PREFIX))
    		  .streamAll()
    		  .filter(blob -> !FileUtils.isGcsDirectory(blob.getName()))
    		  .forEach(this::download);
    }

    private void download(Blob blob) {
        String name = blob.getName();
        Path targetPath = Path.of(LOCAL_STORAGE_PATH, name);

        try {
            FileUtils.createDirectoriesFromPath(targetPath.toString());
            blob.downloadTo(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories or download file: " + name, e);
        }
    }
    
    /*
     * I do not like this approach 
     */
    private void initializeOptionRoot() {
    	log.info("initializeOptionRoot - starting the initialization of assets into main option object");
    	Path localStoragePath = Path.of(LOCAL_STORAGE_PATH);
    	if (Files.notExists(localStoragePath)) {
    		return;
    	}
    	walkInDirectory(localStoragePath.toFile());
    	
    }
    
    // TREBUIE SA VIZUALIZEZ CUM SA PARCURG DRACIA
    private void walkInDirectory(File directory) {
    	File[] files = directory.listFiles();
    	for (File file : files) {
	    	if (file.isDirectory()) {
				OptionDto optionDto = new OptionDto();
				String iconName = "icon-" + file.getName() + ".png";
				Path iconPath = Path.of(file.getParent(), iconName);
				if (Files.exists(iconPath)) {
					optionDto.setIconAssetPath(iconPath.toString());
				}
				
				walkInDirectory(file);
			}
	    	// if file do something else
    	}
    }
}
