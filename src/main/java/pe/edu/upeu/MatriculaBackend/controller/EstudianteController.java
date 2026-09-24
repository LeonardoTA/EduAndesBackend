package pe.edu.upeu.MatriculaBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.EstudianteService;

@RestController
@RequestMapping("/api/v1/estudiantes")
@Tag(name = "Estudiantes", description = "Administración de estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    @Operation(summary = "Listar estudiantes")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<EstudianteResponseDTO>> listar() {
        return ResponseEntity.ok(estudianteService.readAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un estudiante por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<EstudianteResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.read(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Estudiante registrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada"),
        @ApiResponse(responseCode = "409", description = "Código o DNI duplicado")
    })
    public ResponseEntity<EstudianteResponseDTO> crear(@Valid @RequestBody EstudianteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estudianteService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Estudiante o carrera no encontrados"),
        @ApiResponse(responseCode = "409", description = "Código o DNI duplicado")
    })
    public ResponseEntity<EstudianteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstudianteRequestDTO request) {
        return ResponseEntity.ok(estudianteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Estudiante eliminado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado"),
        @ApiResponse(responseCode = "409", description = "El estudiante tiene matrículas asociadas")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estudianteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
