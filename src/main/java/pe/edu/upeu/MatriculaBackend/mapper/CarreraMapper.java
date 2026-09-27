package pe.edu.upeu.MatriculaBackend.mapper;

import pe.edu.upeu.MatriculaBackend.dto.CarreraRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CarreraResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;

public final class CarreraMapper {

    private CarreraMapper() {
    }

    public static void fromRequest(CarreraRequestDTO request, Carrera carrera) {
        carrera.setNombre(request.getNombre().trim());
        carrera.setDescripcion(normalizarOpcional(request.getDescripcion()));
        carrera.setEstado(request.getEstado());
    }

    public static CarreraResponseDTO toResponse(Carrera carrera) {
        CarreraResponseDTO response = new CarreraResponseDTO();
        response.setId(carrera.getId());
        response.setNombre(carrera.getNombre());
        response.setDescripcion(carrera.getDescripcion());
        response.setEstado(carrera.getEstado());
        response.setFechaCreacion(carrera.getFechaCreacion());
        response.setFechaModificacion(carrera.getFechaModificacion());
        return response;
    }

    private static String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }
}
