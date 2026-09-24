package pe.edu.upeu.MatriculaBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Salud", description = "Estado de la API y de su conexión a Oracle")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    @Operation(summary = "Verificar la salud de la API y la conexión con Oracle")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "API y base de datos disponibles"),
        @ApiResponse(responseCode = "503", description = "Base de datos no disponible")
    })
    public ResponseEntity<HealthResponse> health() {
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM DUAL", Integer.class);
            return ResponseEntity.ok(new HealthResponse("UP", "UP", LocalDateTime.now()));
        } catch (Exception exception) {
            log.warn("La verificación de conexión con Oracle falló: {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new HealthResponse("DOWN", "DOWN", LocalDateTime.now()));
        }
    }

    public record HealthResponse(String status, String database, LocalDateTime timestamp) {
    }
}
