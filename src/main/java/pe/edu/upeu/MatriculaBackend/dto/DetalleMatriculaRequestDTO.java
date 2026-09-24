package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMatriculaRequestDTO {

    @NotNull(message = "El curso es obligatorio")
    @Positive(message = "El curso debe tener un identificador valido")
    private Long cursoId;
}
