package com.cubes.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes.api.dto.OptionDto;
import com.cubes.service.StorageService;

@RestController
public class OptionsController {
	
	@Autowired
	private StorageService service;
	
	@GetMapping("options")
	public ResponseEntity<List<OptionDto>> getAllOptions() {
		List<OptionDto> options = 
				service.getOptions().stream()
									.map(OptionDto::toDto)
									.toList();
		return ResponseEntity.ok(options);
	}
}
