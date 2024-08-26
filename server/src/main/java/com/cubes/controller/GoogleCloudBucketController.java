package com.cubes.controller;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes.dto.TempDto;
import com.cubes.service.CubeStorageService;
import com.google.cloud.storage.Blob;

@RestController
public class GoogleCloudBucketController {
	
	@Autowired
	private CubeStorageService cubeStorageService;
   
//    @GetMapping("/files")
//    public ResponseEntity<TempDto> getAllFiles() {
//    	TempDto dto = new TempDto();
//    	
//        Stream<Blob> streamBlob = cubeStorageService.listObjects();
//        streamBlob.forEach(
//        		item -> dto.getFileNames().add(item.getName()));
//        return ResponseEntity.ok(dto);
//    }
    
}