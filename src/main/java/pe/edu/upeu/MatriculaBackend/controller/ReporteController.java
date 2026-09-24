package pe.edu.upeu.MatriculaBackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.MatriculaBackend.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.MatriculaBackend.service.service.ReporteService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/reportes")
@Tag(name = "Reportes", description = "Reportes agregados de matriculas")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/matriculados-por-curso")
    @Operation(
            summary = "Reportar matriculados por curso",
            description = "Incluye solo matriculas REGISTRADA del periodo y permite filtrar por carrera")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte generado"),
        @ApiResponse(responseCode = "400", description = "Parametros invalidos")
    })
    public ResponseEntity<List<MatriculadosPorCursoDTO>> matriculadosPorCurso(
            @Parameter(description = "Periodo academico con formato AAAA-1 o AAAA-2", required = true)
            @Pattern(regexp = "^\\d{4}-[12]$", message = "El periodo debe tener el formato AAAA-1 o AAAA-2")
            @RequestParam String periodo,
            @Parameter(description = "Identificador opcional de la carrera")
            @Positive(message = "La carrera debe tener un identificador valido")
            @RequestParam(required = false) Long carreraId) {
        return ResponseEntity.ok(reporteService.matriculadosPorCurso(periodo, carreraId));
    }
}
