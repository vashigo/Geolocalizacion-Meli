package com.co.mercadolibre.geolocalizacion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones específicas de la aplicación.
     *
     * @param ex la excepción que se produjo.
     * @param request el objeto WebRequest que contiene los detalles de la solicitud.
     * @return un ResponseEntity que contiene los detalles del error y el código de estado HTTP.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja todas las demás excepciones no controladas.
     *
     * @param ex la excepción que se produjo.
     * @param request el objeto WebRequest que contiene los detalles de la solicitud.
     * @return un ResponseEntity que contiene los detalles del error y el código de estado HTTP.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
