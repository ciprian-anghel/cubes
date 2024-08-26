package com.cubes.config;

import org.springframework.beans.factory.annotation.Value;

public class Environment {
	
	@Value("${GOOGLE_CLOUD_STORAGE_PROJECT_ID}")
	private String projectId;
	
	@Value("${GOOGLE_CLOUD_STORAGE_BUCKET_ID}")	
	private String bucketName;
	
	@Value("${CLEAN_AND_REDOWNLOAD_ON_STARTUP:false}")
	private boolean cleanAndRedownloadOnStartup;
	
	public String getProjectId() {
		return this.projectId;
	}
	
	public String getBucketName() {
		return this.bucketName;
	}
	
	public boolean isCleanAndRedownloadOnStartup() {
		return this.cleanAndRedownloadOnStartup;
	}
}
