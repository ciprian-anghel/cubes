package com.cubes.api.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

	private Comparator<OptionDto> sortByCategory = Comparator
				// Directories first
	        .comparing(OptionDto::getTexturePath, Comparator.nullsFirst(Comparator.naturalOrder()))
		        // Compare categories if both are directories. 
	        .thenComparing(a -> OptionCategory.getOptionCategory(a.getCategory()).orElse(null), 
	        		Comparator.nullsFirst(Comparator.comparing(Enum::ordinal))) 
	        	// Fallback
	        .thenComparing(OptionDto::getName); 
		
	@Autowired
	public OptionController(OptionService service) {
		this.service = service;
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
	
}
