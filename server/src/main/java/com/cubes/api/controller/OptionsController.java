package com.cubes.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cubes.domain.entity.Option;
import com.cubes.service.CubeStorageService;

@RestController
public class OptionsController {
	
	@Autowired
	private CubeStorageService service;
	
	@GetMapping("options")
	public ResponseEntity<List<Option>> getAllOptions() {
		return ResponseEntity.ok(service.getOptions());
	}
}
