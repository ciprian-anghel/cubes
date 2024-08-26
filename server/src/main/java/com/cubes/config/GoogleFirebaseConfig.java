package com.cubes.config;

import javax.naming.ConfigurationException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;

@Configuration
public class GoogleFirebaseConfig {
		
	@Bean
	Environment getEnvironment() {
		return new Environment();
	}
	
	@Bean
	FirebaseApp intializeFirebase() {
		FirebaseApp app = FirebaseApp.initializeApp();
		return app;
	}
	
	@Bean
	Bucket getBucket() throws ConfigurationException {
		Storage storage = StorageOptions.newBuilder()
				.setProjectId(getEnvironment().getProjectId())
				.build()
				.getService();
		
		Bucket bucket = storage.get(getEnvironment().getBucketName());
		if (bucket != null) {
			return bucket;
		}
		throw new ConfigurationException("getBucket - Google Cloud Storage's bucket could not be found. Application will not start.");
	}
}