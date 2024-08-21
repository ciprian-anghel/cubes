package com.cubes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;

@RestController
public class GoogleCloudBucketController {

	@Autowired
	private Bucket bucket;
	
	@GetMapping("all")
	public ResponseEntity<String> getAllFiles() {
		List<Blob> blobs = bucket.get(null);
		String name = blobs.get(0).asBlobInfo().getName();
		return ResponseEntity.ok(name);
	}
	
}
