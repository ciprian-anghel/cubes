package com.cubes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes.config.Environment;
import com.cubes.dto.FilesDto;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@RestController
public class GoogleCloudBucketController {

	@Autowired
	private Environment environment;
	
	//TODO: Remove this method
	@GetMapping("all")
	public ResponseEntity<FilesDto> mockToBeRemoved() {
		Storage storage = StorageOptions.newBuilder()
				.setProjectId(environment.getProjectId())
				.build()
				.getService();
		
		FilesDto dto = new FilesDto();
	    Page<Blob> blobs = storage.list(environment.getBucketName(), 
	    		Storage.BlobListOption.prefix("cubes/"));
	    
	    for (Blob blob : blobs.iterateAll()) {
	    	dto.getFileNames().add(blob.getName());
	    }
			
		return ResponseEntity.ok(dto);
	}
	
}

//Use this to return only what contains current directory, one level deep
//Storage.BlobListOption.currentDirectory()
//Page<Blob> blobs = storage.list(environment.getBucketName(), 
//		Storage.BlobListOption.prefix("cubes/"),
//		Storage.BlobListOption.currentDirectory());