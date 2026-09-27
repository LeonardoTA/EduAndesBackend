package pe.edu.upeu.MatriculaBackend.mapper;

import java.util.Locale;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;

public final class EstudianteMapper {

    private EstudianteMapper() {
    }

    public static void fromRequest(EstudianteRequestDTO request, Estudiante estudiante, Carrera carrera) {
        estudiante.setCodigo(request.getCodigo().trim());
        estudiante.setDni(request.getDni().trim());
        estudiante.setNombres(request.getNombres().trim());
        estudiante.setApellidos(request.getApellidos().trim());
        estudiante.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        estudiante.setEstado(request.getEstado());
        estudiante.setCarrera(carrera);
    }

    public static EstudianteResponseDTO toResponse(Estudiante estudiante) {
        EstudianteResponseDTO response = new EstudianteResponseDTO();
        response.setId(estudiante.getId());
        response.setCodigo(estudiante.getCodigo());
        response.setDni(estudiante.getDni());
        response.setNombres(estudiante.getNombres());
        response.setApellidos(estudiante.getApellidos());
        response.setEmail(estudiante.getEmail());
        response.setEstado(estudiante.getEstado());
        response.setCarreraId(estudiante.getCarrera().getId());
        response.setCarreraNombre(estudiante.getCarrera().getNombre());
        response.setFechaCreacion(estudiante.getFechaCreacion());
        response.setFechaModificacion(estudiante.getFechaModificacion());
        return response;
    }
}
