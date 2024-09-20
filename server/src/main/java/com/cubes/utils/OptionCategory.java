package com.cubes.utils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Categories are the directories which should exist in the storage and the mesh names of the 3D model.
 * Directories will be converted to parent options.
 * The order of the parent options is defined by the ordinal of the enum values.
 */
public enum OptionCategory {
	
	HEAD("head"), 
	BODY("body"), 
	FEET("feet"), 
	HAIR("hair"), 
	EYES("eyes"), 
	MOUTH("mouth");
	
	private String category;
	
	private OptionCategory(String category) {
		this.category = category;
	}
	
	public String getCategory() {
		return category;
	}
	
	public static Optional<OptionCategory> getOptionCategory(String category) {
		return Stream.of(OptionCategory.values())
				.filter(o -> o.category.equals(category))
				.findFirst();
	}
	
}