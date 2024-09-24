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
	private final String color;
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
	
	public String getColor() {
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
		private String color;
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
			this.path = removeStaticDirPrefixFromPath(path);
			return this;
		}
		
		public Builder iconPath(String iconPath) {
			if (iconPath == null || iconPath.isBlank()) {
				throw new IllegalArgumentException("iconPath cannot be empty");
			}
			this.iconPath = removeStaticDirPrefixFromPath(iconPath);
			return this;
		}
		
		public Builder texturePath(File texturePath) {
			if (texturePath == null || texturePath.isDirectory()) {
				return this;
			}
			this.texturePath = removeStaticDirPrefixFromPath(texturePath.getPath());
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
		
		public Builder color(String color) {
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
		private String removeStaticDirPrefixFromPath(String path) {
			if (path == null) {
				return null;
			}
			return path.replace("\\", "/")
					   .replace(FirebaseStorageRepository.STATIC_PATH, "");
		}
		
		/* TODO: MOVE THIS TO SOME OTHER HELPER CLASS */
		private String cleanParentPath(String parentPath) {
			if (parentPath == null || isCubesPath(parentPath)) {
				return null;
			}
			return removeStaticDirPrefixFromPath(parentPath);
		}
		
		/* TODO: MOVE THIS TO SOME OTHER HELPER CLASS */
		private boolean isCubesPath(String path) {
		    return Path.of(path).equals(Path.of(FirebaseStorageRepository.CUBES_PATH));
		}
	}
}
