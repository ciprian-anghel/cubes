package com.cubes.service;

import java.io.File;
import java.io.IOException;
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
import org.springframework.stereotype.Service;

import com.cubes.config.Environment;
import com.cubes.domain.entity.Option;
import com.cubes.utils.FileUtils;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage.BlobListOption;


@Service
public class CubeStorageService {
	
	private static final Logger log = LoggerFactory.getLogger(CubeStorageService.class);
	
	private static final String LOCAL_STORAGE_PATH = "localStorage/";
	private static final String FOLDER_PREFIX = "cubes";
	private static final String ICON_PREFIX = "icon-";
	
	private final Bucket bucket;
	private final Environment environment;

	private List<Option> options = new ArrayList<>();
	
	@Autowired
    public CubeStorageService(Bucket bucket, Environment environment) {
        this.bucket = bucket;
        this.environment = environment;
    }
    
    @PostConstruct
    private void postConstruct() {
    	initializeAssets();
    }
    
    public List<Option> getOptions() {
    	return Collections.unmodifiableList(options);
    }
    
    private void initializeAssets() {
    	log.info("initializeAssets - starting to initialize assets");
    	if (environment.isAlwaysDownloadAssets()) {
    		discardAndDownloadAssets();
    	} else {
    		downdloadAssetsIfNotInCache();
    	}
    	processOptions();
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
    
    private void processOptions() {
    	log.info("processOptions - starting the creation of optios");

    	Path cubesPath = Path.of(LOCAL_STORAGE_PATH, FOLDER_PREFIX);
    	if (Files.notExists(cubesPath)) {
    		return;
    	}
    	mapFileToOption(cubesPath.toFile());
    }
    
    private void mapFileToOption(File file) {
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
    			mapFileToOption(f);
    		}
    	}
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
