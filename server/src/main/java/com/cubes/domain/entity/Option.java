package com.cubes.domain.entity;

import java.io.File;
import java.nio.file.Path;

import com.cubes.repository.FirebaseStorageProcessor;
import com.cubes.repository.FirebaseStorageRepository;
import com.cubes.utils.OptionCategory;

public class Option {
	
	private final int id;
	private final String path;
	private final String parentPath;
	private final String iconPath;
	private final String texturePath;
	private final OptionCategory optionCategory;
	private final int color;
	private final String name;
	
	private Option(Builder builder) {
		this.id = builder.id;
		this.parentPath = builder.parentPath;
		this.path = builder.path;
		this.iconPath = builder.iconPath;
		this.texturePath = builder.texturePath;
		this.optionCategory = builder.optionCategory;
		this.color = builder.color;
		this.name = builder.name;	
	}

	public int getId() {
		return id;
	}
	
	public String getParentPath() {
		return parentPath;
	}

	public String getPath() {
		return path;
	}

	public String getIconPath() {
		return iconPath;
	}

	public String getTexturePath() {
		return texturePath;
	}
	
	public OptionCategory getOptionCategory() {
		return optionCategory;
	}
	
	public int getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private int id;
		private String path;
		private String parentPath;
		private String iconPath;
		private String texturePath;
		private OptionCategory optionCategory;
		private int color;
		private String name;
		
		public Builder id(int id) {
			if (id <= 0) {
				throw new IllegalArgumentException("id must be greater than 0");
			}
			this.id = id;
			return this;
		}
		
		public Builder parentPath(String parentPath) {
			if (parentPath == null || parentPath.isBlank()) {
				throw new IllegalArgumentException("parentPath cannot be empty");
			}
			this.parentPath = cleanParentPath(parentPath);
			return this;
		}
		
		public Builder path(String path) {
			if (path == null || path.isBlank()) {
				throw new IllegalArgumentException("path cannot be empty");
			}
			this.path = cleanPath(path);
			return this;
		}
		
		public Builder iconPath(String iconPath) {
			if (iconPath == null || iconPath.isBlank()) {
				throw new IllegalArgumentException("iconPath cannot be empty");
			}
			this.iconPath = cleanPath(iconPath);
			return this;
		}
		
		public Builder texturePath(File texturePath) {
			if (texturePath == null || texturePath.isDirectory()) {
				return this;
			}
			this.texturePath = cleanPath(texturePath.getPath());
			return this;
		}
		
		public Builder category(String path) {
			if (path == null || path.isBlank()) {
				throw new IllegalArgumentException("path for category cannot be empty");
			}
			
			Path filePath = Path.of(path);
			String name;
			if (path.endsWith(FirebaseStorageProcessor.PNG_EXTENTION)) {
				name = filePath.getParent().getName(filePath.getParent().getNameCount() - 1).toString();
			} else {	
				name = filePath.getName(filePath.getNameCount() - 1).toString();		
			}
			this.optionCategory = OptionCategory.getOptionCategory(name).orElseThrow(
					() -> new IllegalArgumentException(String.format("Could not map %s to OptionCategory.", path)));
			return this;
		}
		
		public Builder color(int color) {
			this.color = color;
			return this;
		}
		
		public Builder name(String name) {
			if (name == null || name.isBlank()) {
				throw new IllegalArgumentException("name cannot be empty");
			}
			this.name = name;
			return this;
		}
		
		public Option build() {
			return new Option(this);
		}
		
	    /* TODO: MOVE THIS TO SOME OTHER HELPER CLASS */
		private String cleanPath(String path) {
			if (path == null) {
				return null;
			}
			return path.replace("\\", "/");
//					   .replace(FirebaseStorageRepository.ASSETS_PATH, "");
		}
		
		/* TODO: MOVE THIS TO SOME OTHER HELPER CLASS */
		private String cleanParentPath(String parentPath) {
			if (parentPath == null || isAssetsPath(parentPath)) {
				return null;
			}
			return cleanPath(parentPath);
		}
		
		/* TODO: MOVE THIS TO SOME OTHER HELPER CLASS */
		private boolean isAssetsPath(String path) {
		    return Path.of(path).equals(Path.of(FirebaseStorageRepository.ASSETS_PATH));
		}
	}
}
