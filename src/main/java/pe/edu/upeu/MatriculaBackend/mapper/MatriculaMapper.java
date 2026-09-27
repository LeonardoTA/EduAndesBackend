package pe.edu.upeu.MatriculaBackend.mapper;

import java.math.RoundingMode;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.entity.DetalleMatricula;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;

public final class MatriculaMapper {

    private MatriculaMapper() {
    }

    public static MatriculaResponseDTO toResponse(Matricula matricula) {
        MatriculaResponseDTO response = new MatriculaResponseDTO();
        response.setId(matricula.getId());
        response.setFecha(matricula.getFecha());
        response.setPeriodo(matricula.getPeriodo());
        response.setEstudianteId(matricula.getEstudiante().getId());
        response.setEstudianteCodigo(matricula.getEstudiante().getCodigo());
        response.setEstudianteNombre(
                matricula.getEstudiante().getNombres() + " " + matricula.getEstudiante().getApellidos());
        response.setEstado(matricula.getEstado());
        response.setTotalCreditos(matricula.getTotalCreditos());
        response.setMontoTotal(matricula.getMontoTotal().setScale(2, RoundingMode.HALF_UP));
        response.setDetalles(matricula.getDetalles().stream()
                .map(MatriculaMapper::toDetalleResponse)
                .toList());
        response.setFechaCreacion(matricula.getFechaCreacion());
        response.setFechaModificacion(matricula.getFechaModificacion());
        return response;
    }

    private static DetalleMatriculaResponseDTO toDetalleResponse(DetalleMatricula detalle) {
        Curso curso = detalle.getCurso();
        return new DetalleMatriculaResponseDTO(
                detalle.getId(),
                curso.getId(),
                curso.getCodigo(),
                curso.getNombre(),
                detalle.getCreditos(),
                detalle.getCosto().setScale(2, RoundingMode.HALF_UP));
    }
}
