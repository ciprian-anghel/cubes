package com.cubes.api.dto;

public class OptionDto {

	private int id;
	private String path;
	private String parentPath;
	private String iconPath;
	private String texturePath;
	private String category;
	private String modelCategory;
	private int color;
	private int renderOrder;
	private String name;

	public OptionDto() {}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getModelCategory() {
		return modelCategory;
	}

	public void setModelCategory(String modelCategory) {
		this.modelCategory = modelCategory;
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	public int getRenderOrder() {
		return renderOrder;
	}
	
	public void setRenderOrder(int renderOrder) {
		this.renderOrder = renderOrder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
