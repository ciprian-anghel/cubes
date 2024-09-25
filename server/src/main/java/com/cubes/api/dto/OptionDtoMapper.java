package com.cubes.api.dto;

import com.cubes.domain.entity.Option;
import com.cubes.utils.OptionCategory;

public class OptionDtoMapper {
		
	public static OptionDto apply(Option option) {
		OptionDto dto = new OptionDto();
		dto.setId(option.getId());
		dto.setPath(option.getPath());
		dto.setParentPath(option.getParentPath());
		dto.setIconPath(option.getIconPath());
		dto.setTexturePath(option.getTexturePath());
		dto.setColor(option.getColor());
		dto.setName(option.getName());
		
		OptionCategory category = option.getOptionCategory();
		if (category != null) {			
			dto.setRenderOrder(category.getRenderOrder());
			dto.setCategory(category.getCategory());		
			OptionCategory modelCategory = category.getModelCategory();
			if (modelCategory != null) {
				dto.setModelCategory(modelCategory.getCategory());
			}
		}
		
		return dto;
	}
	
}
