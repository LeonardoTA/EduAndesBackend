package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

public class MatriculaRequestDTO {

    @NotNull(message = "El estudiante es obligatorio")
    @Positive(message = "El estudiante debe tener un identificador valido")
    private Long estudianteId;

    @NotNull(message = "El periodo es obligatorio")
    @Pattern(regexp = "^\\d{4}-[12]$", message = "El periodo debe tener el formato AAAA-1 o AAAA-2")
    private String periodo;

    @Valid
    @NotEmpty(message = "La matricula debe incluir al menos un curso")
    private List<DetalleMatriculaRequestDTO> detalles = new ArrayList<>();

    public MatriculaRequestDTO() {
    }

    public MatriculaRequestDTO(
            Long estudianteId,
            String periodo,
            List<DetalleMatriculaRequestDTO> detalles) {
        this.estudianteId = estudianteId;
        this.periodo = periodo;
        this.detalles = detalles;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public List<DetalleMatriculaRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleMatriculaRequestDTO> detalles) {
        this.detalles = detalles;
    }
}
