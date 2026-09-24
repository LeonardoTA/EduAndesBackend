package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
