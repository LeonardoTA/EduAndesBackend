package pe.edu.upeu.MatriculaBackend.mapper;

import pe.edu.upeu.MatriculaBackend.dto.CursoRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Curso;

public final class CursoMapper {

    private CursoMapper() {
    }

    public static void fromRequest(CursoRequestDTO request, Curso curso, Carrera carrera) {
        curso.setCodigo(request.getCodigo().trim());
        curso.setNombre(request.getNombre().trim());
        curso.setCreditos(request.getCreditos());
        curso.setCiclo(request.getCiclo());
        curso.setVacantes(request.getVacantes());
        curso.setEstado(request.getEstado());
        curso.setCarrera(carrera);
    }

    public static CursoResponseDTO toResponse(Curso curso) {
        CursoResponseDTO response = new CursoResponseDTO();
        response.setId(curso.getId());
        response.setCodigo(curso.getCodigo());
        response.setNombre(curso.getNombre());
        response.setCreditos(curso.getCreditos());
        response.setCiclo(curso.getCiclo());
        response.setVacantes(curso.getVacantes());
        response.setEstado(curso.getEstado());
        response.setCarreraId(curso.getCarrera().getId());
        response.setCarreraNombre(curso.getCarrera().getNombre());
        response.setFechaCreacion(curso.getFechaCreacion());
        response.setFechaModificacion(curso.getFechaModificacion());
        return response;
    }
}
