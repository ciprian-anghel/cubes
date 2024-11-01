package com.cubes.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	public Option getOption(int id) {
		Optional<Option> optionOpt = repository.getOptions().stream().filter(o -> o.getId() == id).findFirst();
		if (optionOpt.isEmpty()) {
			throw new NoSuchElementException("Option with id was not found: " + id);
		}
		return optionOpt.get();
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
	
	public Set<String> getCategories() {
		return getAllOptions().stream()
				.map(o -> o.getOptionCategory().getCategory())
				.collect(Collectors.toSet());
	}
	
}
