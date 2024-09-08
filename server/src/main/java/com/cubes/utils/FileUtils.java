package com.cubes.utils;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class FileUtils {
	
	public void deleteDirectory(File dirPath) {
        if (dirPath.exists()) {
            for (File file : dirPath.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                }
                file.delete();
            }
            dirPath.delete();
        }
    }
	
}
