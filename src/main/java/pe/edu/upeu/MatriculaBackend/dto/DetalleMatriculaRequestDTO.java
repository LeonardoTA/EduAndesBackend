package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DetalleMatriculaRequestDTO {

    @NotNull(message = "El curso es obligatorio")
    @Positive(message = "El curso debe tener un identificador valido")
    private Long cursoId;

    public DetalleMatriculaRequestDTO() {
    }

    public DetalleMatriculaRequestDTO(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }
}
