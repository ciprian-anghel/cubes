package com.cubes.service;

import java.io.InputStream;
import java.util.stream.Stream;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage.BlobListOption;

public class CubeStorageService {
	
	private static final String FOLDER_PREFIX = "cubes/";
	
    private final Bucket bucket;

    public CubeStorageService(Bucket bucket) {
        this.bucket = bucket;
    }

    private String withPrefix(String objectName) {
        return FOLDER_PREFIX + objectName;
    }

    public Blob getObject(String objectName) {
        return bucket.get(withPrefix(objectName));
    }

    public Blob createObject(String objectName, InputStream content) {
        return bucket.create(withPrefix(objectName), content);
    }

    public void deleteObject(String objectName) {
        bucket.get(withPrefix(objectName)).delete();
    }
    
    public Stream<Blob> listObjects() {
    	return bucket.list(BlobListOption.prefix(FOLDER_PREFIX)).streamAll();
    }
}
