package pe.edu.upeu.MatriculaBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matriculas")
@Tag(name = "Matriculas", description = "Registro y anulacion de matriculas academicas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    @Operation(summary = "Registrar una matricula con sus cursos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Matricula registrada"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "404", description = "Estudiante o curso no encontrado"),
        @ApiResponse(responseCode = "409", description = "Regla de matricula incumplida")
    })
    public ResponseEntity<MatriculaResponseDTO> crear(
            @Valid @RequestBody MatriculaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar matriculas con sus detalles")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<MatriculaResponseDTO>> listar() {
        return ResponseEntity.ok(matriculaService.readAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una matricula por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matricula encontrada"),
        @ApiResponse(responseCode = "404", description = "Matricula no encontrada")
    })
    public ResponseEntity<MatriculaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.read(id));
    }

    @PatchMapping("/{id}/anular")
    @Operation(summary = "Anular una matricula y devolver las vacantes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matricula anulada"),
        @ApiResponse(responseCode = "404", description = "Matricula no encontrada"),
        @ApiResponse(responseCode = "409", description = "La matricula ya estaba anulada")
    })
    public ResponseEntity<MatriculaResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.anular(id));
    }
}
