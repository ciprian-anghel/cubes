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
	BASE_COLOR("color", null, 0),
	HEAD("head", null, 0),
	BODY("body", null, 0),
	FEET("feet", null, 0),
	
	//head elements
	HAIR("hair", HEAD, 1),
	EYES("eyes", HEAD, 2),
	MOUTH("mouth", HEAD, 3),
	
	//body elements
	SHIRT("shirt", BODY, 1),
	NECKLACE("necklace", BODY, 2),
	
	//feet elements
	PANTS("pants", FEET, 1),
	BELT("belt", FEET, 2),
	SHOES("shoes", FEET, 3);
	
	private String category;
	private OptionCategory modelCategory;
	private int renderOrder;
	
	private OptionCategory(String category, OptionCategory modelCategory, int renderOrder) {
		this.category = category;
		this.modelCategory = modelCategory;
		this.renderOrder = renderOrder;
	}
	
	/**
	 * There should be max one texture applied in UI for each category at any given time.
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * @return The model's part to which the texture will be applied in UI.
	 */
	public OptionCategory getModelCategory() {
		return modelCategory;
	}
	
	/**
	 * @return the order in which the textures are applied under the same model category
	 */
	public int getRenderOrder() {
		return renderOrder;
	}
	
	public static Optional<OptionCategory> getOptionCategory(String category) {
		return Stream.of(OptionCategory.values())
				.filter(o -> o.category.equals(category))
				.findFirst();
	}
	
}