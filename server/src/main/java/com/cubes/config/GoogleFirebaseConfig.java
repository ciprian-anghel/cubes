package com.cubes.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class GoogleFirebaseConfig {
	
	public static final String GC_STORAGE_BUCKET_ID = "GOOGLE_CLOUD_STORAGE_BUCKET_ID";
	public static final String GC_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS";
	
	@Autowired
    private Environment environment;
	
//	@PostConstruct
//	public void postConstruct() {
//		String path = environment.getProperty(GC_APPLICATION_CREDENTIALS, "");
//		if (path != null && !path.isBlank() && Files.notExists(Path.of(path))) {
//			throw new IllegalArgumentException(
//					String.format("Environment property %s is set but the file does not exist.", GC_APPLICATION_CREDENTIALS));
//		}
//	}
	
	@Bean
	FirebaseApp intializeFirebase() throws IOException {
		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.getApplicationDefault())
				.setStorageBucket(environment.getProperty(GC_STORAGE_BUCKET_ID))
				.build();
		
		return FirebaseApp.initializeApp(options);
	}
	
	@Bean
	Bucket getBucket() throws ConfigurationException {		
		Storage storage = StorageOptions.getDefaultInstance().getService();
		
		Bucket bucket = storage.get(environment.getProperty(GC_STORAGE_BUCKET_ID));
		if (bucket != null) {
			return bucket;
		}
		throw new ConfigurationException("getBucket - Google Cloud Storage's bucket could not be found. Application will not start.");
	}
}