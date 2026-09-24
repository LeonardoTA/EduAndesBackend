package pe.edu.upeu.MatriculaBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import pe.edu.upeu.MatriculaBackend.dto.CursoRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;

@RestController
@Validated
@RequestMapping("/api/v1/cursos")
@Tag(name = "Cursos", description = "Administración y búsqueda de cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    @Operation(summary = "Listar cursos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<CursoResponseDTO>> listar() {
        return ResponseEntity.ok(cursoService.readAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un curso por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso encontrado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<CursoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.read(id));
    }

    @GetMapping("/buscar")
    @Operation(
            summary = "Buscar cursos",
            description = "Combina filtros opcionales y ordena por nombre, creditos o vacantes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada"),
        @ApiResponse(responseCode = "400", description = "Filtro, campo o dirección de orden inválidos")
    })
    public ResponseEntity<List<CursoResponseDTO>> buscar(
            @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
            @RequestParam(required = false) String nombre,
            @Positive(message = "La carrera debe tener un identificador válido")
            @RequestParam(required = false) Long carreraId,
            @Min(value = 1, message = "El ciclo debe ser al menos 1")
            @Max(value = 10, message = "El ciclo no puede superar 10")
            @RequestParam(required = false) Integer ciclo,
            @RequestParam(required = false) Boolean conVacantes,
            @Parameter(description = "nombre, creditos o vacantes")
            @Pattern(regexp = "^(?i:nombre|creditos|vacantes)$", message = "El orden no es válido")
            @RequestParam(defaultValue = "nombre") String orden,
            @Parameter(description = "asc o desc")
            @Pattern(regexp = "^(?i:asc|desc)$", message = "La dirección no es válida")
            @RequestParam(defaultValue = "asc") String dir) {
        return ResponseEntity.ok(cursoService.buscar(nombre, carreraId, ciclo, conVacantes, orden, dir));
    }

    @PostMapping
    @Operation(summary = "Crear un curso")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Curso creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada"),
        @ApiResponse(responseCode = "409", description = "Código duplicado")
    })
    public ResponseEntity<CursoResponseDTO> crear(@Valid @RequestBody CursoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un curso")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Curso actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Curso o carrera no encontrados"),
        @ApiResponse(responseCode = "409", description = "Código duplicado")
    })
    public ResponseEntity<CursoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequestDTO request) {
        return ResponseEntity.ok(cursoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un curso sin matrículas asociadas")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Curso eliminado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado"),
        @ApiResponse(responseCode = "409", description = "El curso tiene matrículas asociadas")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
