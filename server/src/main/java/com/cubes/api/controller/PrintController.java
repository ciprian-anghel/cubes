package com.cubes.api.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;


@RestController
public class PrintController {
	
	private static final Logger log = LoggerFactory.getLogger(PrintController.class);
	
	@GetMapping("/print")
	public ResponseEntity<FileSystemResource> createPrintableImage() throws IOException { 
		
		// Stack the images
		BufferedImage img1 = ImageIO.read(new File("src/main/resources/static/test/Untitled-2.png"));
		BufferedImage img2 = ImageIO.read(new File("src/main/resources/static/test/Untitled-2.png"));
        
//        int width = Math.max(img1.getWidth(), img2.getWidth());
//        int height = img1.getHeight() + img2.getHeight();
		
		//A4 300dpi
		int width = 2480;
		int height = 3508;
        int gap = 100;
        int margin = gap;
		
        BufferedImage stackedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = stackedImage.createGraphics();
        
        g2d.drawImage(img1, margin, margin, null);
        g2d.drawImage(img2, margin, margin + img1.getHeight() + gap, null);
        g2d.dispose();
		
        // Save the final stacked image to a temporary file
        LocalDateTime timestamp = LocalDateTime.now();
		String fileName = "printable-" + timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".png";
		File outputFile = new File(fileName);
		ImageIO.write(stackedImage, "png", outputFile);
		
		// Return the image as a file download
		FileSystemResource resource = new FileSystemResource(outputFile);
		log.debug("Printable file created: " + resource);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		
		ResponseEntity<FileSystemResource> responseEntity = ResponseEntity
				.ok()
				.headers(headers)
				.contentLength(outputFile.length())
				.body(resource);
		
		//create some async task to delete the files ?
		
		return responseEntity;
	}

}
