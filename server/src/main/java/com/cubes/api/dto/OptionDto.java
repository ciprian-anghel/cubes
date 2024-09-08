package com.cubes.api.dto;

import com.cubes.domain.entity.Option;
import com.cubes.service.StorageService;

public class OptionDto {

	private String path;
	private String parentPath;
	private String iconPath;
	private String texturePath;
	private String name;

	private OptionDto() {
	}

	public static OptionDto toDto(Option option) {
		OptionDto dto = new OptionDto();
		dto.setPath(parsePathForJson(option.getPath()));
		dto.setParentPath(parsePathForJson(option.getParentPath()));
		dto.setIconPath(parsePathForJson(option.getIconPath()));
		dto.setTexturePath(parsePathForJson(option.getTexturePath()));
		dto.setName(option.getName());
		return dto;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private static String parsePathForJson(String path) {
		if (path != null) {
			return path.toString().replace("\\", "/")
								  .replace(StorageService.STATIC_PATH, "");
		}
		return path;
	}

}
