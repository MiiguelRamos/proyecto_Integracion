package aiss.videominer.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


 // Manejador global de excepciones de VideoMiner.

 // Centraliza el formato de respuesta para errores que no están ya cubiertos por una excepción ya hecha (...NotFoundException)

@ControllerAdvice
public class GlobalExceptionHandler {


     // Se dispara cuando el cuerpo de la petición no es válida (Ej: nombre de Channel vacío o campos obligatorios ausentes)

     // Devuelve 400 Bad Request con un JSON limpio que lista los mensajes de validación.

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, List<String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        Map<String, List<String>> body = new HashMap<>();
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


     // Se dispara cuando una entidad no cumple una restricción de la base de datos (Ej: id duplicado)
     // Devuelve 409 Conflict en lugar de un 500.

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        Map<String, String> body = new HashMap<>();
        String cause = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();
        body.put("error", "Data integrity violation: " + cause);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}
