package com.cubes.api.dto;

import java.util.ArrayList;
import java.util.List;

public class TempDto {

	private List<String> fileNames = new ArrayList<>();

	public List<String> getFileNames() {
		return fileNames;
	}

	public void addFileName(String fileName) {
		fileNames.add(fileName);
	}
	
}
