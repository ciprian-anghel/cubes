package com.cubes.api.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cubes.api.dto.OptionDto;
import com.cubes.api.dto.OptionDtoMapper;
import com.cubes.domain.entity.Option;
import com.cubes.service.OptionService;
import com.cubes.utils.OptionCategory;

@RestController
public class OptionController {
	
	private static final Logger log = LoggerFactory.getLogger(OptionController.class);
	
	private OptionService service;
	
	private Comparator<Option> sortByCategory = Comparator
			// Directories first
			.comparing(Option::getTexturePath, Comparator.nullsFirst(Comparator.naturalOrder()))
			// Compare categories if both are directories. 
			.thenComparing(o -> OptionCategory.getOptionCategory(o.getOptionCategory().getCategory()).orElse(null),
				Comparator.nullsFirst(Comparator.comparing(Enum::ordinal)))
			// Fallback
			.thenComparing(Option::getName);

	@Autowired
	public OptionController(OptionService service) {
		this.service = service;
	}

	@GetMapping("/all-options")
	public ResponseEntity<List<OptionDto>> getAllOptions() {
		List<OptionDto> options = 
				service.getAllOptions().stream()
					.sorted(sortByCategory)
					.map(OptionDtoMapper::apply)
					.toList();
		return ResponseEntity.ok(options);
	}

	@GetMapping("/root-options")
	public ResponseEntity<List<OptionDto>> getRootOptions() {
		List<OptionDto> options = 
				service.getRootOptions().stream()
					.sorted(sortByCategory)
					.map(OptionDtoMapper::apply)				
					.toList();		
		return ResponseEntity.ok(options);
	}

	@GetMapping("/children")
	public ResponseEntity<List<OptionDto>> getChildren(@RequestParam int id) {
		List<OptionDto> options = 
				service.getChildrenOf(id).stream()
				.sorted(sortByCategory)
				.map(OptionDtoMapper::apply)
				.toList();
		return ResponseEntity.ok(options);
	}

	@GetMapping(value="/asset", 
				produces = {MediaType.IMAGE_PNG_VALUE, 
						MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getAsset(@RequestParam String path) throws IOException {
		File resource = Path.of(path).toFile();
		if (!resource.exists()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.contentType(MediaType.APPLICATION_JSON)
					.body(new FileNotFoundException("File not found: " + path));
		}

		try {
			log.debug("asset - get image: " + path);
			
			InputStream inputStream = new BufferedInputStream(new FileInputStream(resource));
			InputStreamResource inputStreamResource = new InputStreamResource(inputStream);	
			
			CacheControl caching = CacheControl.maxAge(365, TimeUnit.DAYS);
			return ResponseEntity.ok()
					.cacheControl(caching)
					.contentType(MediaType.IMAGE_PNG)
					.body(inputStreamResource); //stream is closed by spring
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .contentType(MediaType.APPLICATION_JSON)
	                .body(new IOException("Error reading the file: " + path));
		}
	}

}
