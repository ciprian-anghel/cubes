package com.cubes.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import com.cubes.config.Environment;
import com.google.cloud.storage.Bucket;

public class CubeStorageServiceTestCase {

	@Mock private Bucket bucketMock;
	@Mock private Environment environmentMock;
	
	private CubeStorageService service;
	
	@BeforeEach
	public void init() {
		service = new CubeStorageService(bucketMock, environmentMock);
	}
	
//	@Test
//	public void testMapPathToObject() {
//		Path path = Path.of("localStorage/", "cubes/");
//		service.mapFileToOption(path.toFile(), 0);
//	}
	
}
