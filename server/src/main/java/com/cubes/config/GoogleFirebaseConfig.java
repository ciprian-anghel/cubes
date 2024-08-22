package com.cubes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class GoogleFirebaseConfig {
	
	//READ THIS: https://firebase.google.com/docs/admin/setup#java
	//Clean Up pom.xml dependencies. Maybe run a mvn command to see which dependencies are not used.
	
	@Bean
	Bucket createBucket() {
		Storage storage = StorageOptions.getDefaultInstance().getService();
		String bucketName = "testfirebase-b3a7e.appspot.com";
		return storage.create(BucketInfo.of(bucketName));
	}	
	
//	void intializeFirebase() {
//		FirebaseOptions options = FirebaseOptions.builder()
//				.setProjectId("testfirebase-b3a7e")
//				.set
//		FirebaseApp app = FirebaseApp.initializeApp();
//			}
//	
}
