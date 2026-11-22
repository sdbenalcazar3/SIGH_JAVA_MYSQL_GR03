package edu.ec.monster.ORM_JAVA_GR03.controlador;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class ErroresControlador {
    @ExceptionHandler({DataIntegrityViolationException.class, org.springframework.orm.ObjectRetrievalFailureException.class})
    public ResponseEntity<?> integridad(RuntimeException ex) { return ResponseEntity.status(409).body(Map.of("mensaje","La operación viola una restricción: referencia inexistente, valor duplicado o registro utilizado por otra tabla.")); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validacion(MethodArgumentNotValidException ex) { return ResponseEntity.badRequest().body(Map.of("mensaje","Revise los campos obligatorios y sus longitudes.","campos",ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField()).distinct().sorted().toList())); }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> formato(HttpMessageNotReadableException ex) { return ResponseEntity.badRequest().body(Map.of("mensaje","El JSON contiene tipos o fechas inválidos.")); }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> estado(ResponseStatusException ex) { return ResponseEntity.status(ex.getStatusCode()).body(Map.of("mensaje",ex.getReason()==null ? "Operación rechazada" : ex.getReason())); }
}
