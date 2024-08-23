package com.cubes.config;

import java.util.Optional;

import javax.naming.ConfigurationException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.gax.paging.Page;
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
	    Storage storage = StorageOptions.newBuilder().setProjectId(getEnvironment().getProjectId()).build().getService();

	    Page<Bucket> buckets = storage.list();
	    Optional<Bucket> optionalBucket = buckets.streamAll()
	    		.filter(b -> b.getName().endsWith(getEnvironment().getBucketName()))
	    		.findFirst();
	    
	    if (optionalBucket.isPresent()) {
	    	return optionalBucket.get();
	    }
	    throw new ConfigurationException("getBucket - Google Cloud Storage's bucket could not be found. Application will not start.");
	}
	
}


//	@Bean
//	Bucket createBucket() {
//		Storage storage = StorageOptions.getDefaultInstance().getService();
//		String bucketName = "testfirebase-b3a7e.appspot.com";
//		return storage.create(BucketInfo.of(bucketName));
//	}	

//		FirebaseOptions options = FirebaseOptions.builder()
//				.setProjectId("testfirebase-b3a7e");