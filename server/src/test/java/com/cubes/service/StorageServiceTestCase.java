package com.cubes.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.cubes.repository.FirebaseStorageProcessor;
import com.cubes.repository.FirebaseStorageRepository;
import com.cubes.utils.FileUtils;
import com.google.cloud.storage.Bucket;

@SpringBootTest
class StorageServiceTestCase {
	
	@Mock private Bucket bucketMock;
	@Mock private Environment environmentMock;
	@Mock private FirebaseStorageProcessor processorMock;
	@Mock private FileUtils fileUtils;
	
	@InjectMocks
	private FirebaseStorageRepository service;
	
	@Test
	public void testInitializeAssets() {
		
	}
	
}
