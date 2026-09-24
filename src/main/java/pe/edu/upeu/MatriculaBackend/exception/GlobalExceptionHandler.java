package pe.edu.upeu.MatriculaBackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pe.edu.upeu.MatriculaBackend.exception.dto.ErrorResponseDTO;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarReglaNegocio(
            ReglaNegocioException exception,
            HttpServletRequest request) {
        log.warn("Regla de negocio rechazada en {}: {}", request.getRequestURI(), exception.getMessage());
        return respuesta(HttpStatus.CONFLICT, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidacion(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errores.merge(
                        error.getField(),
                        error.getDefaultMessage() == null ? "Valor inválido" : error.getDefaultMessage(),
                        (primero, siguiente) -> primero + "; " + siguiente));
        return respuesta(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos", request, errores);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRestricciones(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation ->
                errores.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return respuesta(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos", request, errores);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarSolicitudInvalida(
            Exception exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "La solicitud no tiene el formato esperado", request, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> manejarIntegridad(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        log.warn("Conflicto de integridad de datos en {}", request.getRequestURI());
        return respuesta(
                HttpStatus.CONFLICT,
                "La operación entra en conflicto con datos existentes",
                request,
                Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErrorInesperado(
            Exception exception,
            HttpServletRequest request) {
        log.error("Error inesperado en {}", request.getRequestURI(), exception);
        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno. Intente nuevamente más tarde",
                request,
                Map.of());
    }

    private ResponseEntity<ErrorResponseDTO> respuesta(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErrors);
        return ResponseEntity.status(status).body(body);
    }
}
