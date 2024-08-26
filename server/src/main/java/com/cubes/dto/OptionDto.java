package com.cubes.dto;

import java.util.Set;

public class OptionDto {

	private String nameId;
	private String path;	
	private String iconAssetPath;
	private String assetPath;
	private int level;
	private Set<OptionDto> children;
	
	public String getNameId() {
		return nameId;
	}
	
	public void setNameId(String nameId) {
		this.nameId = nameId;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getIconAssetPath() {
		return iconAssetPath;
	}
	
	public void setIconAssetPath(String iconAssetPath) {
		this.iconAssetPath = iconAssetPath;
	}
	
	public String getAssetPath() {
		return assetPath;
	}
	
	public void setAssetPath(String assetPath) {
		this.assetPath = assetPath;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public Set<OptionDto> getChildren() {
		return children;
	}
	
	public void setChildren(Set<OptionDto> children) {
		this.children = children;
	}	
}
