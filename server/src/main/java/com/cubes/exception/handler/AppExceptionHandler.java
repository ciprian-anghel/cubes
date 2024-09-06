package com.cubes.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cubes.exception.AppException;
import com.cubes.exception.ErrorDto;

@ControllerAdvice
public class AppExceptionHandler {

	@ExceptionHandler(value = {AppException.class})
	@ResponseBody
	public ResponseEntity<ErrorDto> handleAppException(AppException e) {
		return ResponseEntity.status(e.getHttpStatus())
				.body(new ErrorDto(e.getMessage()));
	}
}