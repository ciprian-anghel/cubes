package com.cubes.domain.entity;

import java.io.File;

public class Option {

	private final String path;
	private final String parentPath;
	private final String iconPath;
	private final String texturePath;
	private final String name;
	
	private Option(Builder builder) {
		this.path = builder.path;
		this.parentPath = builder.parentPath;
		this.iconPath = builder.iconPath;
		this.texturePath = builder.texturePath;
		this.name = builder.name;	
	}

	public String getPath() {
		return path;
	}

	public String getParentPath() {
		return parentPath;
	}

	public String getIconPath() {
		return iconPath;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public String getName() {
		return name;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String path;
		private String parentPath;
		private String iconPath;
		private String texturePath;
		private String name;
		
		public Builder path(String path) {
			if (path == null || path.isBlank()) 
				throw new IllegalArgumentException("path cannot be empty");
			this.path = path;
			return this;
		}
		
		public Builder parentPath(String parentPath) {
			if (parentPath == null || parentPath.isBlank()) 
				throw new IllegalArgumentException("parentPath cannot be empty");
			this.parentPath = parentPath;
			return this;
		}
		
		public Builder iconPath(String iconPath) {
			if (iconPath == null || iconPath.isBlank()) 
				throw new IllegalArgumentException("iconPath cannot be empty");
			this.iconPath = iconPath;
			return this;
		}
		
		public Builder texturePath(File texturePath) {
			if (texturePath == null || texturePath.isDirectory()) 
				return this;
			
			this.texturePath = texturePath.getPath();
			return this;
		}
		
		public Builder name(String name) {
			if (name == null || name.isBlank()) 
				throw new IllegalArgumentException("name cannot be empty");
			this.name = name;
			return this;
		}
		
		public Option build() {
			return new Option(this);
		}
	}
}