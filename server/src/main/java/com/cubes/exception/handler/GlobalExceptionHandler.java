package com.cubes.exception.handler;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cubes.exception.ErrorDto;

@Order(LOWEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = {Exception.class})
	@ResponseBody
	public ResponseEntity<ErrorDto> handleAppException(Exception e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorDto("Unexpected server error."));
	}
}