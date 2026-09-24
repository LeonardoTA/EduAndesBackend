package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
public class CursoRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^[A-Z]{2}\\d{3}$", message = "El código debe tener dos letras mayúsculas y tres dígitos")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Los créditos deben ser al menos 1")
    @Max(value = 6, message = "Los créditos no pueden superar 6")
    private Integer creditos;

    @NotNull(message = "El ciclo es obligatorio")
    @Min(value = 1, message = "El ciclo debe ser al menos 1")
    @Max(value = 10, message = "El ciclo no puede superar 10")
    private Integer ciclo;

    @NotNull(message = "Las vacantes son obligatorias")
    @PositiveOrZero(message = "Las vacantes no pueden ser negativas")
    private Integer vacantes;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

    @NotNull(message = "La carrera es obligatoria")
    @Positive(message = "La carrera debe tener un identificador válido")
    private Long carreraId;

    public CursoRequestDTO() {
    }

    public CursoRequestDTO(
            String codigo,
            String nombre,
            Integer creditos,
            Integer ciclo,
            Integer vacantes,
            Boolean estado,
            Long carreraId) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.creditos = creditos;
        this.ciclo = ciclo;
        this.vacantes = vacantes;
        this.estado = estado;
        this.carreraId = carreraId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Integer getCiclo() {
        return ciclo;
    }

    public void setCiclo(Integer ciclo) {
        this.ciclo = ciclo;
    }

    public Integer getVacantes() {
        return vacantes;
    }

    public void setVacantes(Integer vacantes) {
        this.vacantes = vacantes;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Long getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(Long carreraId) {
        this.carreraId = carreraId;
    }
}
