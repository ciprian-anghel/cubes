package com.cubes.config;

import org.springframework.beans.factory.annotation.Value;

public class Environment {
	
	@Value("${GOOGLE_CLOUD_STORAGE_PROJECT_ID}")
	public String projectId;
	
	@Value("${GOOGLE_CLOUD_STORAGE_BUCKET_ID}")	
	public String bucketName;
	
	public String getProjectId() {
		return this.projectId;
	}
	
	public String getBucketName() {
		return this.bucketName;
	}

}
