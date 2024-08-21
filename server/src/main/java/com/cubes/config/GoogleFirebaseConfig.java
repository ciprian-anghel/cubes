package com.cubes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class GoogleFirebaseConfig {

	@Bean
	Bucket createBucket() {
		Storage storage = StorageOptions.getDefaultInstance().getService();
//	    String bucketName = "gs://testfirebase-b3a7e.appspot.com";
		String bucketName = "testfirebase-b3a7e.appspot.com";
		return storage.create(BucketInfo.of(bucketName));
	}
	
}
