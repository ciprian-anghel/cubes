package com.cubes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cubes.domain.entity.Option;
import com.cubes.exception.AppException;
import com.cubes.repository.FirebaseStorageRepository;

@Service
public class OptionService {

	private FirebaseStorageRepository repository;
	
	@Autowired
	public OptionService(FirebaseStorageRepository repository) {
		this.repository = repository;
	}
	
	public List<Option> getAllOptions() {
		return repository.getOptions();
	}
	
	
	//TODO: Fix me, parentPath nu are null in acest loc.
	//		Fix, muta logica de clean din DTO in FirebaseStorageProcessor
	public List<Option> getRootOptions() {
		return getAllOptions().stream()
				.filter(o -> o.getParentPath() == null).toList(); 
	}
	
	public List<Option> getChildrenOf(int id)  {
		Optional<Option> parentOption = getAllOptions().stream()
				.filter(o -> o.getId() == id).findFirst();
		
		if (parentOption.isEmpty()) {
			throw new AppException(
					String.format("Asset having id = {} was not found.", id), HttpStatus.NOT_FOUND);
		}
		
		String parentPath = parentOption.get().getPath();
		
		return getAllOptions().stream()
				.filter(o -> parentPath.equals(o.getParentPath())).toList();
	}
	
}
