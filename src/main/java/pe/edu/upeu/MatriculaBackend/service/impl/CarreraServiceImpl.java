package pe.edu.upeu.MatriculaBackend.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.CarreraRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CarreraResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.service.service.CarreraService;

@Service
public class CarreraServiceImpl implements CarreraService {

    private static final Logger log = LoggerFactory.getLogger(CarreraServiceImpl.class);

    private final CarreraRepository carreraRepository;
    private final CursoRepository cursoRepository;

    public CarreraServiceImpl(CarreraRepository carreraRepository, CursoRepository cursoRepository) {
        this.carreraRepository = carreraRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    @Transactional
    public CarreraResponseDTO create(CarreraRequestDTO request) {
        String nombre = normalizarNombre(request.getNombre());
        validarNombreDisponible(nombre, null);

        Carrera carrera = new Carrera();
        copiarDatos(request, carrera, nombre);
        Carrera guardada = carreraRepository.save(carrera);
        log.info("Carrera creada con id {}", guardada.getId());
        return toResponse(guardada);
    }

    @Override
    @Transactional
    public CarreraResponseDTO update(Long id, CarreraRequestDTO request) {
        Carrera carrera = buscarEntidad(id);
        String nombre = normalizarNombre(request.getNombre());
        validarNombreDisponible(nombre, id);

        copiarDatos(request, carrera, nombre);
        Carrera actualizada = carreraRepository.save(carrera);
        log.info("Carrera actualizada con id {}", id);
        return toResponse(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraResponseDTO read(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Carrera carrera = buscarEntidad(id);
        if (cursoRepository.existsByCarreraId(id)) {
            log.warn("No se puede eliminar la carrera {} porque tiene cursos asociados", id);
            throw new ReglaNegocioException("No se puede eliminar la carrera porque tiene cursos asociados");
        }
        carreraRepository.delete(carrera);
        log.info("Carrera eliminada con id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraResponseDTO> readAll() {
        return carreraRepository.findAll().stream().map(this::toResponse).toList();
    }

    private Carrera buscarEntidad(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con id " + id));
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        boolean duplicado = idActual == null
                ? carreraRepository.existsByNombreIgnoreCase(nombre)
                : carreraRepository.existsByNombreIgnoreCaseAndIdNot(nombre, idActual);
        if (duplicado) {
            log.warn("Nombre de carrera duplicado: {}", nombre);
            throw new ReglaNegocioException("Ya existe una carrera con el nombre indicado");
        }
    }

    private void copiarDatos(CarreraRequestDTO request, Carrera carrera, String nombre) {
        carrera.setNombre(nombre);
        carrera.setDescripcion(normalizarOpcional(request.getDescripcion()));
        carrera.setEstado(request.getEstado());
    }

    private String normalizarNombre(String valor) {
        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }

    private CarreraResponseDTO toResponse(Carrera carrera) {
        CarreraResponseDTO response = new CarreraResponseDTO();
        response.setId(carrera.getId());
        response.setNombre(carrera.getNombre());
        response.setDescripcion(carrera.getDescripcion());
        response.setEstado(carrera.getEstado());
        response.setFechaCreacion(carrera.getFechaCreacion());
        response.setFechaModificacion(carrera.getFechaModificacion());
        return response;
    }
}
