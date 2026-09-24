package pe.edu.upeu.MatriculaBackend.dto;

import java.math.BigDecimal;

public class DetalleMatriculaResponseDTO {

    private Long id;
    private Long cursoId;
    private String cursoCodigo;
    private String cursoNombre;
    private Integer creditos;
    private BigDecimal costo;

    public DetalleMatriculaResponseDTO() {
    }

    public DetalleMatriculaResponseDTO(
            Long id,
            Long cursoId,
            String cursoCodigo,
            String cursoNombre,
            Integer creditos,
            BigDecimal costo) {
        this.id = id;
        this.cursoId = cursoId;
        this.cursoCodigo = cursoCodigo;
        this.cursoNombre = cursoNombre;
        this.creditos = creditos;
        this.costo = costo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoCodigo() {
        return cursoCodigo;
    }

    public void setCursoCodigo(String cursoCodigo) {
        this.cursoCodigo = cursoCodigo;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
}
