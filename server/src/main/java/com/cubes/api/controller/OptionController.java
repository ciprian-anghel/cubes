package com.cubes.api.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes.api.dto.OptionDto;
import com.cubes.api.dto.OptionDtoMapper;
import com.cubes.service.OptionService;
import com.cubes.utils.OptionCategory;

@RestController
public class OptionController {
	
	private OptionService service;
	private ResourceLoader resourceLoader;

	private Comparator<OptionDto> sortByCategory = Comparator
				// Directories first
	        .comparing(OptionDto::getTexturePath, Comparator.nullsFirst(Comparator.naturalOrder()))
		        // Compare categories if both are directories. 
	        .thenComparing(a -> OptionCategory.getOptionCategory(a.getCategory()).orElse(null), 
	        		Comparator.nullsFirst(Comparator.comparing(Enum::ordinal))) 
	        	// Fallback
	        .thenComparing(OptionDto::getName); 
		
	@Autowired
	public OptionController(OptionService service, ResourceLoader resourceLoader) {
		this.service = service;
		this.resourceLoader = resourceLoader;
	}
	
	@GetMapping("/all-options")
	public ResponseEntity<List<OptionDto>> getAllOptions() {
		List<OptionDto> options = 
				service.getAllOptions()
					.stream().map(OptionDtoMapper::apply)
					.sorted(sortByCategory)
					.toList();
		return ResponseEntity.ok(options);
	}
	
	@GetMapping("/root-options")
	public ResponseEntity<List<OptionDto>> getRootOptions() {
		List<OptionDto> options = 
				service.getRootOptions().stream()
					.map(OptionDtoMapper::apply)
					.sorted(sortByCategory)
					.toList();		
		return ResponseEntity.ok(options);
	}
	
	@GetMapping("/children")
	public ResponseEntity<List<OptionDto>> getChildren(@RequestParam int id) {
		List<OptionDto> options = 
				service.getChildrenOf(id).stream()
					.map(OptionDtoMapper::apply)
					.sorted(sortByCategory)
					.toList();
		return ResponseEntity.ok(options);
	}
	
	@GetMapping("/asset")
	public ResponseEntity<?> listPaths(@RequestParam String path) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:/static" + path);
		if (resource.exists() && resource.isFile()) {
			File file = resource.getFile();
			InputStreamResource inputStreamResource = 
					new InputStreamResource(new FileInputStream(file));
			HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_PNG);  // Adjust the media type as needed
	        headers.setContentLength(file.length());

	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(inputStreamResource);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + path);
		}
	}
	
}
