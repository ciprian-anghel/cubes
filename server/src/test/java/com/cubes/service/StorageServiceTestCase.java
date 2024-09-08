package com.cubes.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.cubes.config.Environment;
import com.cubes.utils.FileUtils;
import com.google.cloud.storage.Bucket;

@SpringBootTest
class StorageServiceTestCase {
	
	@Mock private Bucket bucketMock;
	@Mock private Environment environmentMock;
	@Mock private StorageProcessor processorMock;
	@Mock private FileUtils fileUtils;
	
	@InjectMocks
	private StorageService service;
	
	@Test
	public void testInitializeAssets() {
		
	}
	
}
