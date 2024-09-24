package com.cubes.utils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Representation of all type of categories of options.
 * 
 * The order of the values inside {@code OptionCategory} defines the order of elements in the response JSON, 
 * and in the end the order of the elements in the UI.
 * 
 * The same directory names must exist in the storage having names set as {@code category}.
*/
public enum OptionCategory {
	
	//top elements
	BASE_COLOR("color", null),
	HEAD("head", null),
	BODY("body", null),
	FEET("feet", null),
	
	//head elements
	HAIR("hair", HEAD),
	EYES("eyes", HEAD),
	MOUTH("mouth", HEAD),
	
	//body elements
	SHIRT("shirt", BODY),
	NECKLACE("necklace", BODY),
	
	//feet elements
	BELT("belt", FEET),
	PANTS("pants", FEET),
	SHOES("shoes", FEET);
	
	private String category;
	private OptionCategory modelCategory;
	
	private OptionCategory(String category, OptionCategory modelCategory) {
		this.category = category;
		this.modelCategory = modelCategory;
	}
	
	public String getCategory() {
		return category;
	}
	
	public OptionCategory getModelCategory() {
		return modelCategory;
	}
	
	public static Optional<OptionCategory> getOptionCategory(String category) {
		return Stream.of(OptionCategory.values())
				.filter(o -> o.category.equals(category))
				.findFirst();
	}
	
}