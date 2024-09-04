package com.cubes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.cubes.service.CubeStorageService;

public class Environment {
	
	private static final Logger log = LoggerFactory.getLogger(Environment.class);
	
	@Value("${GOOGLE_CLOUD_STORAGE_PROJECT_ID}")
	private String projectId;
	
	@Value("${GOOGLE_CLOUD_STORAGE_BUCKET_ID}")	
	private String bucketName;
	
	@Value("${ALWAYS_DOWNLOAD_ASSETS:false}")
	private boolean alwaysDownloadAssets;
	
	public String getProjectId() {
		return this.projectId;
	}
	
	public String getBucketName() {
		return this.bucketName;
	}
	
	/**
	 * If {@code true}, previously downloaded assets should be removed
	 * and downloaded again after each application startup.
	 */
	public boolean isAlwaysDownloadAssets() {
		if (this.alwaysDownloadAssets) {
			log.warn("***************************************");
			log.warn("!!! ASSETS ARE SET TO BE DOWNLOADED !!!");
			log.warn("!!!   AFTER EACH INSTANCE RESTART.  !!!");
			log.warn("!!!    EXTRA CHARGES MIGHT OCCUR.   !!!");
			log.warn("***************************************");
		}
		return this.alwaysDownloadAssets;
	}
}
