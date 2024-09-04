package com.cubes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes.dto.TempDto;
import com.cubes.service.CubeStorageService;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;

@RestController
public class GoogleCloudBucketController {
	
	@Autowired
	private CubeStorageService cubeStorageService;
	
	@Autowired
	private Bucket bucket;
	
    @GetMapping("/test")
    public ResponseEntity<TempDto> getAllFiles() {
    	TempDto dto = new TempDto();
    	Page<Blob> blobs = bucket.list();
    	blobs.streamAll().forEach(b -> dto.addFileName(b.getName()));
        return ResponseEntity.ok(dto);
    }
    
}