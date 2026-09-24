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
import pe.edu.upeu.MatriculaBackend.dto.CarreraRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CarreraResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.CarreraService;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;

@RestController
@RequestMapping("/api/v1/carreras")
@Tag(name = "Carreras", description = "Administración de carreras académicas")
public class CarreraController {

    private final CarreraService carreraService;
    private final CursoService cursoService;

    public CarreraController(CarreraService carreraService, CursoService cursoService) {
        this.carreraService = carreraService;
        this.cursoService = cursoService;
    }

    @GetMapping
    @Operation(summary = "Listar carreras")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<CarreraResponseDTO>> listar() {
        return ResponseEntity.ok(carreraService.readAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una carrera por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrera encontrada"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada")
    })
    public ResponseEntity<CarreraResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.read(id));
    }

    @GetMapping("/{id}/cursos")
    @Operation(summary = "Listar los cursos de una carrera")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada")
    })
    public ResponseEntity<List<CursoResponseDTO>> listarCursos(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.findByCarreraId(id));
    }

    @PostMapping
    @Operation(summary = "Crear una carrera")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carrera creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<CarreraResponseDTO> crear(@Valid @RequestBody CarreraRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carreraService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una carrera")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrera actualizada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    public ResponseEntity<CarreraResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CarreraRequestDTO request) {
        return ResponseEntity.ok(carreraService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una carrera sin cursos asociados")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Carrera eliminada"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada"),
        @ApiResponse(responseCode = "409", description = "La carrera tiene cursos asociados")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carreraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
