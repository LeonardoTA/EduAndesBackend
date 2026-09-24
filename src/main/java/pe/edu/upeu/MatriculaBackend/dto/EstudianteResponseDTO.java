package pe.edu.upeu.MatriculaBackend.dto;

import java.time.LocalDateTime;
public class EstudianteResponseDTO {
    private Long id;
    private String codigo;
    private String dni;
    private String nombres;
    private String apellidos;
    private String email;
    private Boolean estado;
    private Long carreraId;
    private String carreraNombre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public EstudianteResponseDTO() {
    }

    public EstudianteResponseDTO(
            Long id,
            String codigo,
            String dni,
            String nombres,
            String apellidos,
            String email,
            Boolean estado,
            Long carreraId,
            String carreraNombre,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaModificacion) {
        this.id = id;
        this.codigo = codigo;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.estado = estado;
        this.carreraId = carreraId;
        this.carreraNombre = carreraNombre;
        this.fechaCreacion = fechaCreacion;
        this.fechaModificacion = fechaModificacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCarreraNombre() {
        return carreraNombre;
    }

    public void setCarreraNombre(String carreraNombre) {
        this.carreraNombre = carreraNombre;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}
