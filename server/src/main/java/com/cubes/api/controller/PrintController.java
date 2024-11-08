package com.cubes.api.controller;
import static com.cubes.repository.FirebaseStorageRepository.PRINT_PATH;

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
import com.cubes.service.OptionService;
import com.cubes.utils.OptionCategory;


@RestController
public class PrintController {
	
	private static final Logger log = LoggerFactory.getLogger(PrintController.class);
	
	private static final String HEAD_CUTOUTS_PATH = PRINT_PATH + "/cutouts/head.png";
	private static final String BODY_CUTOUTS_PATH = PRINT_PATH + "/cutouts/body.png";
	private static final String FEET_CUTOUTS_PATH = PRINT_PATH + "/cutouts/feet.png";
	private static final String HEAD_MASK_PATH = PRINT_PATH + "/mask/head.png";
	private static final String BODY_MASK_PATH = PRINT_PATH + "/mask/body.png";
	private static final String FEET_MASK_PATH = PRINT_PATH + "/mask/feet.png";
	
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
			@RequestParam(required = false, defaultValue = "") List<Integer> ids 
	) throws IOException {
		
		try (PDDocument document = new PDDocument()) {
			final File headCutout = getFileResource(HEAD_CUTOUTS_PATH);
			final File bodyCutout = getFileResource(BODY_CUTOUTS_PATH);
			final File feetCutout = getFileResource(FEET_CUTOUTS_PATH);
			
			final File headMask = getFileResource(HEAD_MASK_PATH);
			final File bodyMask = getFileResource(BODY_MASK_PATH);
			final File feetMask = getFileResource(FEET_MASK_PATH);
			
			Map<OptionCategory, Set<Option>> printMap = printableElementsMap(ids);
			logPrintableOptions(printMap);
			
			int baseColor = getBaseColor(printMap.get(OptionCategory.BASE_COLOR));
					
			printMap.forEach((key, options) -> {
				if (key == OptionCategory.HEAD) {
					drawCategory(options, document, headCutout, headMask, baseColor);
				} else if (key == OptionCategory.BODY) {
					drawCategory(options, document, bodyCutout, bodyMask, baseColor);
				} else if (key == OptionCategory.FEET) {
					drawCategory(options, document, feetCutout, feetMask, baseColor);
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
	
	private int getBaseColor(Set<Option> options) {
		if (options == null || options.isEmpty()) {
			return 0;
		}
		return options.stream().findFirst().get().getColor();
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
		
	private File getFileResource(String path) throws IOException {
		File result = new File(path);
		if (!result.exists()) {
	        throw new IOException("Resource not found: " + path);
	    }
		return result;
	}
	
	private Map<OptionCategory, Set<Option>> printableElementsMap(List<Integer> ids) {
		Map<OptionCategory, Set<Option>> result = ids.stream().map(id -> optionService.getOption(id))
		.collect(
				Collectors.toMap(
							(key) -> {
								if (key.getOptionCategory().getModelCategory() == null) {
									return OptionCategory.BASE_COLOR;
								}
								return key.getOptionCategory().getModelCategory();
							}, 
							(option) -> {
								Set<Option> optionSet = new TreeSet<>(compareByRenderOrder);
								if (option.getTexturePath() != null || option.getColor() > 0) {
									optionSet.add(option);
								}
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
		
		if (result.get(OptionCategory.BASE_COLOR) == null) {
			result.put(OptionCategory.BASE_COLOR, Set.of());
		}
		
		return result;
	}

	private void drawCategory(Set<Option> options, PDDocument document, File cutouts, File mask, int rgbColor) {
		PDPage page = new PDPage(A4_300DPI);
		document.addPage(page);
		
		BufferedImage stackedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = null;
		try {
			g2d = stackedImage.createGraphics();
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
	        drawBaseColor(rgbColor, g2d);
	        
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
	
	private void drawBaseColor(int rgbColor, Graphics2D g2d) {
		if (rgbColor <= 0) {
			return;
		}
		g2d.setColor(new Color(rgbColor));
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		g2d.setComposite(AlphaComposite.SrcOver);
	}
	
	private void drawAsset(String texturePath, Graphics2D g2d) throws IOException {
		if (texturePath == null) {
			return;
		}
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
