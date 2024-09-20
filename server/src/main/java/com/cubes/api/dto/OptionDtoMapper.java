package com.cubes.api.dto;

import com.cubes.domain.entity.Option;

public class OptionDtoMapper {
		
	public static OptionDto apply(Option option) {
		OptionDto dto = new OptionDto();
		dto.setId(option.getId());
		dto.setPath(option.getPath());
		dto.setParentPath(option.getParentPath());
		dto.setIconPath(option.getIconPath());
		dto.setTexturePath(option.getTexturePath());
		dto.setCategory(option.getCategory());
		dto.setName(option.getName());
		return dto;
	}
	
}
