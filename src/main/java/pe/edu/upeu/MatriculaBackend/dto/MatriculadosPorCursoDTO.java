package pe.edu.upeu.MatriculaBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculadosPorCursoDTO {

    private String codigo;
    private String curso;
    private Long matriculados;
    private BigDecimal montoRecaudado;
}
