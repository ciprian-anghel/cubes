package com.cubes.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.cubes.exception.ErrorDto;
import com.cubes.service.StorageProcessor;

@ControllerAdvice
public class AssetNotFoundExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(AssetNotFoundExceptionHandler.class);

	@ExceptionHandler(value = {NoResourceFoundException.class})
	@ResponseBody
	public ResponseEntity<ErrorDto> handleAppException(NoResourceFoundException e) {
		log.warn("Asset was not found - {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorDto(e.getMessage()));
	}
}