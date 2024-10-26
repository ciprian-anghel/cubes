package com.cubes.api.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import com.cubes.domain.entity.Option;
import com.cubes.exception.AppException;
import com.cubes.repository.FirebaseStorageRepository;
import com.cubes.service.OptionService;
import com.cubes.utils.OptionCategory;


@RestController
public class PrintController {
	
	private static final Logger log = LoggerFactory.getLogger(PrintController.class);
	
	public static final String PRINT_PATH = FirebaseStorageRepository.CUBES_PATH + "/print";
	
	@Autowired
	private OptionService optionService;
	
	/*
	 * 53 	= hair-1
	 * 51 	= eye-2
	 * 63 	= mouth-3
	 * 47 	= beard-1
	 * 3 	= shirt-1-v3
	 * 39 	= pants-1-v2
	 * 44 	= shoes-1
	 */
	
	/*
	 * 1. create a PDF with 3 pages: page one is for head, page two for body, three for feet
	 * 2. read all selected options and based on the category print on the desired page
	 * 3. add pixels remover at the end (TBD)
	 * 4. save
	 */
	@GetMapping("/print")
	public ResponseEntity<FileSystemResource> createPrintableImage(
			@RequestParam List<Integer> ids
	) throws IOException {
		//TODO: Extract this
		Comparator<Option> compareByRenderOrder = 
				Comparator.comparing((Option o) -> o.getOptionCategory().getRenderOrder());
		
		//TODO: Extract this
		Map<OptionCategory, Set<Option>> printMap = 
				ids.stream().map(id -> optionService.getOption(id))
				.collect(
						Collectors.toMap(
									(option) -> option.getOptionCategory().getModelCategory(), 
									(option) -> {
										Set<Option> optionSet = new TreeSet<>(compareByRenderOrder);
										optionSet.add(option);
										return optionSet;
									},
									(existingSet, newSet) -> {
										existingSet.addAll(newSet);
										return existingSet;
									}
				));
		
		logPrintableOptions(printMap);
		
		log.info(PRINT_PATH);
		
		//A4 300dpi
		int widthInPx = 2480;
		int heightInPx = 3508;
		
		try (PDDocument document = new PDDocument()) {
			
			//Create 3 blank pages
			PDPage page1 = new PDPage(PDRectangle.A4);
			PDPage page2 = new PDPage(PDRectangle.A4);
			PDPage page3 = new PDPage(PDRectangle.A4);
			
			document.addPage(page1);
			document.addPage(page2);
			document.addPage(page3);
			
			Consumer<Option> optionConsumer = 
					(option) -> {
						System.out.println(option.getTexturePath());
					};
					
//			printMap.get(OptionCategory.HEAD).forEach(
//			);
			
			LocalDateTime generationDate = LocalDateTime.now();
			String generatedDateString = generationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
			String fileName = String.format("%s/ready-to-print-model-%s.pdf", PRINT_PATH, generatedDateString);
			document.save(fileName);
			log.info(String.format("Printable file generated: ",document));
			
			File outputFile = new File(fileName);
			FileSystemResource resource = new FileSystemResource(outputFile);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			
			return ResponseEntity
					.ok()
					.headers(headers)
					.contentLength(outputFile.length())
					.body(resource);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException("Printable file could not be generated.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	@PostMapping("/print")
//	public ResponseEntity<FileSystemResource> createPrintableImage_old(@RequestParam List<Integer> ids) throws IOException {
//		
//		List<Option> printableOptions = ids.stream()
//										 .map(id -> optionService.getOption(id))
//										 .toList();
//		
//		//A4 300dpi
//		int width = 2480;
//		int height = 3508;
////        int gap = 100;
////        int margin = gap;
//		
//		try (PDDocument document = new PDDocument()) {
//			
//			//Create 3 blank pages
//			PDPage page1 = new PDPage(PDRectangle.A4);
//			PDPage page2 = new PDPage(PDRectangle.A4);
//			PDPage page3 = new PDPage(PDRectangle.A4);
//			
//			document.addPage(page1);
//			document.addPage(page2);
//			document.addPage(page3);
//			
//			drawOnPage(document, page1, 1);
//			drawOnPage(document, page2, 2);
//			drawOnPage(document, page3, 3);
//			
//			LocalDateTime generationDate = LocalDateTime.now();
//			String generatedDateString = generationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
//			document.save(String.format("ready-to-print-model-%s.pdf", generatedDateString));
//			log.info(String.format("Printable file generated: ",document));
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new AppException("Printable file could not be generated.", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
//		ResponseEntity<FileSystemResource> responseEntity = ResponseEntity
//				.ok()
//				.headers(headers)
//				.contentLength(outputFile.length())
//				.body(resource);
		
		//-----------------------------
		
//        BufferedImage stackedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2d = stackedImage.createGraphics();
//        
//        //convert to stream
//        for (Option o : printableOptions) {
//        	
//        }
//        
//        //One paper per category
//        
////        g2d.drawImage(img1, margin, margin, null);
////        g2d.drawImage(img2, margin, margin + img1.getHeight() + gap, null);
//        g2d.dispose();
//		
//        // Save the final stacked image to a temporary file
//        LocalDateTime timestamp = LocalDateTime.now();
//		String fileName = "printable-" + timestamp.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".png";
//		File outputFile = new File(fileName);
//		ImageIO.write(stackedImage, "png", outputFile);
//		
//		// Return the image as a file download
//		FileSystemResource resource = new FileSystemResource(outputFile);
//		log.debug("Printable file created: " + resource);
//		HttpHeaders headers = new HttpHeaders();
//		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
//		
//		ResponseEntity<FileSystemResource> responseEntity = ResponseEntity
//				.ok()
//				.headers(headers)
//				.contentLength(outputFile.length())
//				.body(resource);
//		
//		//create some async task to delete the files ?
//		
//		return responseEntity;
//	}
	
	private void drawOnPage(PDDocument document, PDPage page, int pageNumber) throws IOException {
		int width = (int) page.getMediaBox().getWidth();
		int height = (int) page.getMediaBox().getHeight();
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = bufferedImage.createGraphics();
		
		if (true) {
//			 g2d.drawImage(img1, 0, 0, null);
		}
		
		g2d.dispose();
		
		// Convert BufferedImage to PDF image and draw it on the page
        PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.drawImage(pdImage, 0, 0, width, height);
        }
		
	}
	
	private void logPrintableOptions(Map<OptionCategory, Set<Option>> printMap) {
		printMap.forEach((key, options) -> {
			options.forEach(
				 option -> log.debug(
						 String.format("Print - %s - id %s - render order %s - texture %s", 
								 key, 
								 option.getId(), 
								 option.getOptionCategory().getRenderOrder(), 
								 option.getTexturePath())));
		});
	}

}
