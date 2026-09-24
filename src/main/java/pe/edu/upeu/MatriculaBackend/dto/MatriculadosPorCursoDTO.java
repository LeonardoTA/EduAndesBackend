package pe.edu.upeu.MatriculaBackend.dto;

import java.math.BigDecimal;

public class MatriculadosPorCursoDTO {

    private String codigo;
    private String curso;
    private Long matriculados;
    private BigDecimal montoRecaudado;

    public MatriculadosPorCursoDTO() {
    }

    public MatriculadosPorCursoDTO(
            String codigo,
            String curso,
            Long matriculados,
            BigDecimal montoRecaudado) {
        this.codigo = codigo;
        this.curso = curso;
        this.matriculados = matriculados;
        this.montoRecaudado = montoRecaudado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Long getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(Long matriculados) {
        this.matriculados = matriculados;
    }

    public BigDecimal getMontoRecaudado() {
        return montoRecaudado;
    }

    public void setMontoRecaudado(BigDecimal montoRecaudado) {
        this.montoRecaudado = montoRecaudado;
    }
}
