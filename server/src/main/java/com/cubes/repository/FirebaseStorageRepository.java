package com.cubes.repository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.cubes.domain.entity.Option;
import com.cubes.exception.AppException;
import com.cubes.utils.FileUtils;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage.BlobListOption;

/**
 * Class which downloads the assets from remote storage and keep the processed options metadata.
 */
@Repository
public class FirebaseStorageRepository {
	
	private static final Logger log = LoggerFactory.getLogger(FirebaseStorageRepository.class);
	
	public static final String BASE_PATH = Path.of(System.getProperty("user.dir"), "../", "cube_assets").normalize().toString();
	private static final String ASSETS_PATH = BASE_PATH + "/assets";
	public static final String CUBES_PATH = ASSETS_PATH + "/cubes";
	private static final String FOLDER_PREFIX = "cubes";
	
	private final Bucket bucket;
	private final Environment environment;
	private final FirebaseStorageProcessor processor;
	private final FileUtils fileUtils;

	private List<Option> options;
	
	@Autowired
    public FirebaseStorageRepository(Bucket bucket, Environment environment, FirebaseStorageProcessor processor, FileUtils fileUtils) {
        this.bucket = bucket;
        this.environment = environment;
        this.processor = processor;
        this.fileUtils = fileUtils;
    }
    
    @PostConstruct
    private void postConstruct() {
    	options = initializeOptions();
    }
    
    public List<Option> getOptions() {
    	return Collections.unmodifiableList(options);
    }
    
    public List<Option> initializeOptions() {
    	log.info("Starting to initialize assets");
    	if (Boolean.getBoolean(environment.getProperty("ALWAYS_DOWNLOAD_ASSETS", "false"))) {
    		discardCachedAssets(Paths.get(CUBES_PATH).toFile());
    	}    		
    	downloadAssetsIfNotCached();
    	return processor.processOptions(Path.of(CUBES_PATH));
    }
    
    private void discardCachedAssets(File file) {
    	log.info("discardCachedAssets - remove cached assets");
    	fileUtils.deleteDirectory(file);
    }
    
    private void downloadAssetsIfNotCached() {
        if (cacheFolderExists()) {
            log.info("Cache found.");
            return;
        }
        log.info("Cache not found, downloading assets...");
        downloadAssetsFromRemoteStorage();
    }
    
    private boolean cacheFolderExists() {
    	return Files.exists(Path.of(CUBES_PATH));
    }
    
    private void downloadAssetsFromRemoteStorage() {
    	log.info("Downloading assets from remote storage");
    	bucket.list(BlobListOption.prefix(FOLDER_PREFIX))
    		  .streamAll()
    		  .filter(blob -> processor.isAllowedFile(blob.getName()))
    		  .parallel()
    		  .forEach(this::downloadAsset);
    }

    private void downloadAsset(Blob blob) {
        String name = blob.getName();
        Path targetPath = Path.of(ASSETS_PATH, name);

        try {
        	processor.createDirectoriesFromPath(targetPath.toString());
            blob.downloadTo(targetPath);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new AppException(
            		String.format("Failed to create directories or download file: %s", name), 
            		HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
