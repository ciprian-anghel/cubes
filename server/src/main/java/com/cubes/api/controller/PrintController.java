package com.cubes.api.controller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
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
	
	private static final String PRINT_PATH = FirebaseStorageRepository.BASE_PATH + "/print";
	private static final Path STATIC_DIRECTORY = Path.of("static");
	private static final Path HEAD_CUTOUTS_PATH = Path.of(STATIC_DIRECTORY.toString(), "/cutouts/head.png");
	private static final Path BODY_CUTOUTS_PATH = Path.of(STATIC_DIRECTORY.toString(), "/cutouts/body.png");
	private static final Path FEET_CUTOUTS_PATH = Path.of(STATIC_DIRECTORY.toString(), "/cutouts/feet.png");
	private static final Path HEAD_MASK_PATH = Path.of(STATIC_DIRECTORY.toString(), "/mask/head.png");
	private static final Path BODY_MASK_PATH = Path.of(STATIC_DIRECTORY.toString(), "/mask/body.png");
	private static final Path FEET_MASK_PATH = Path.of(STATIC_DIRECTORY.toString(), "/mask/feet.png");
	
	//This corresponds to an A4 image with 300dpi
	private static final int WIDTH = 2480;
	private static final int HEIGHT = 3508;
	private static final PDRectangle A4_300DPI = new PDRectangle(WIDTH, HEIGHT);

	private Comparator<Option> compareByRenderOrder = 
			Comparator.comparing((Option o) -> o.getOptionCategory().getRenderOrder());
	
	@Autowired
	private OptionService optionService;
		
	@GetMapping("/print")
	public ResponseEntity<StreamingResponseBody> createPrintableImage(
			@RequestParam(required = false, defaultValue = "ffffff") String baseColor,
			@RequestParam(required = false, defaultValue = "") List<Integer> ids 
	) throws IOException {
		
		try (PDDocument document = new PDDocument()) {
			final File headCutout = getFileResource(HEAD_CUTOUTS_PATH);
			final File bodyCutout = getFileResource(BODY_CUTOUTS_PATH);
			final File feetCutout = getFileResource(FEET_CUTOUTS_PATH);
			
			final File headMask = getFileResource(HEAD_MASK_PATH);
			final File bodyMask = getFileResource(BODY_MASK_PATH);
			final File feetMask = getFileResource(FEET_MASK_PATH);
			
			Map<OptionCategory, Set<Option>> printMap = createPrintMap(ids);
			logPrintableOptions(printMap);
		
			printMap.forEach((key, options) -> {
				if (key == OptionCategory.HEAD) {
					drawCategory(options, document, headCutout, headMask, baseColor);
				} else if (key == OptionCategory.BODY) {
					drawCategory(options, document, bodyCutout, bodyMask, baseColor);
				} else if (key == OptionCategory.FEET) {
					drawCategory(options, document, feetCutout, feetMask, baseColor);
				} else {
					throw new AppException(
							"Unsupported category: " + key.getCategory(), HttpStatus.BAD_REQUEST);
				}
			});

			FileSystemResource resource = generatePdfResource(document);
			
	        StreamingResponseBody responseBody = outputStream -> {
	        	final Path resourcePath = resource.getFile().toPath();
	            Files.copy(resourcePath, outputStream);
	            outputStream.flush();
	            Files.deleteIfExists(resourcePath);
	        };
	        
	        HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
			
			return ResponseEntity.ok()
					.headers(headers)
					.contentLength(resource.getFile().length())
					.body(responseBody);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException("Printable file could not be generated.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private FileSystemResource generatePdfResource(PDDocument document) throws IOException {
		File printDir = new File(PRINT_PATH);
		if (!printDir.exists()) {
			printDir.mkdirs();
		}
		
		LocalDateTime generationDate = LocalDateTime.now();
		String generatedDateString = generationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
		String fileName = String.format("ready-to-print-model-%s.pdf", generatedDateString);
		String filePath = Path.of(PRINT_PATH, fileName).toString();
		
		document.save(filePath);
		log.info(String.format("Print file generated: ", filePath));
		
		File outputFile = new File(filePath);
		return new FileSystemResource(outputFile);
	}
	
	private File getFileResource(Path path) throws IOException {
		return new ClassPathResource(path.toString()).getFile();
	}
		
	private Map<OptionCategory, Set<Option>> createPrintMap(List<Integer> ids) {
		Map<OptionCategory, Set<Option>> result = ids.stream().map(id -> optionService.getOption(id))
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
		
		if (result.get(OptionCategory.HEAD) == null) {
			result.put(OptionCategory.HEAD, Set.of());
		}
		
		if (result.get(OptionCategory.BODY) == null) {
			result.put(OptionCategory.BODY, Set.of());
		}
		
		if (result.get(OptionCategory.FEET) == null) {
			result.put(OptionCategory.FEET, Set.of());
		}
		
		return result;
	}

	private void drawCategory(Set<Option> options, PDDocument document, File cutouts, File mask, String hexColor) {
		PDPage page = new PDPage(A4_300DPI);
		document.addPage(page);
		
		BufferedImage stackedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = null;
		try {
			g2d = stackedImage.createGraphics();
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
	        drawBaseColor(hexColor, g2d);
	        
			for (Option o : options) {
				drawAsset(o.getTexturePath(), g2d);
			}

			drawMask(mask, g2d);
			drawCutouts(cutouts, g2d);
			drawStackedImagesToPdfPage(document, page, stackedImage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (g2d != null) {
				g2d.dispose();
			}
		}
	}
	
	private void drawBaseColor(String hexColor, Graphics2D g2d) {
		g2d.setColor(new Color(Integer.parseInt(hexColor, 16)));
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		g2d.setComposite(AlphaComposite.SrcOver);
	}
	
	private void drawAsset(String texturePath, Graphics2D g2d) throws IOException {
		BufferedImage image = ImageIO.read(new File(texturePath));
		g2d.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
	}
	
	/**
	 * Clears all pixels outside of the dark area of mask. This should be used to clear out all the texture spillovers.
	 * Method should be called BEFORE drawing the cutouts.
	 */
	private void drawMask(File mask, Graphics2D g2d) throws IOException {
		BufferedImage maskBufferedImage = ImageIO.read(mask);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN, 1.0f));
        g2d.drawImage(maskBufferedImage, 0, 0, WIDTH, HEIGHT, null);
            
        // Reset the composite to default to avoid affecting subsequent drawings
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
	}
	
	private void drawCutouts(File cutouts, Graphics2D g2d) throws IOException {
		BufferedImage cutoutsBufferedImage = ImageIO.read(cutouts);
		g2d.drawImage(cutoutsBufferedImage, 0, 0, WIDTH, HEIGHT, null);
	}
	
	private void drawStackedImagesToPdfPage(PDDocument document, PDPage page, BufferedImage stackedImage) throws IOException {
		PDImageXObject pdImage = LosslessFactory.createFromImage(document, stackedImage);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.drawImage(pdImage, 0, 0, WIDTH, HEIGHT);
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
