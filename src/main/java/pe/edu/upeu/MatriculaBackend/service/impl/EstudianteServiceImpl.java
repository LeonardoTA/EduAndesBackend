package pe.edu.upeu.MatriculaBackend.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.mapper.EstudianteMapper;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.service.service.EstudianteService;

@Service
public class EstudianteServiceImpl implements EstudianteService {

    private static final Logger log = LoggerFactory.getLogger(EstudianteServiceImpl.class);

    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;

    public EstudianteServiceImpl(
            EstudianteRepository estudianteRepository,
            CarreraRepository carreraRepository) {
        this.estudianteRepository = estudianteRepository;
        this.carreraRepository = carreraRepository;
    }

    @Override
    @Transactional
    public EstudianteResponseDTO create(EstudianteRequestDTO request) {
        String codigo = request.getCodigo().trim();
        String dni = request.getDni().trim();
        validarUnicidad(codigo, dni, null);
        Carrera carrera = buscarCarrera(request.getCarreraId());

        Estudiante estudiante = new Estudiante();
        EstudianteMapper.fromRequest(request, estudiante, carrera);
        Estudiante guardado = estudianteRepository.save(estudiante);
        log.info("Estudiante creado con id {}", guardado.getId());
        return EstudianteMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public EstudianteResponseDTO update(Long id, EstudianteRequestDTO request) {
        Estudiante estudiante = buscarEntidad(id);
        String codigo = request.getCodigo().trim();
        String dni = request.getDni().trim();
        validarUnicidad(codigo, dni, id);
        Carrera carrera = buscarCarrera(request.getCarreraId());

        EstudianteMapper.fromRequest(request, estudiante, carrera);
        Estudiante actualizado = estudianteRepository.save(estudiante);
        log.info("Estudiante actualizado con id {}", id);
        return EstudianteMapper.toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteResponseDTO read(Long id) {
        return EstudianteMapper.toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Estudiante estudiante = buscarEntidad(id);
        estudianteRepository.delete(estudiante);
        log.info("Estudiante eliminado con id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteResponseDTO> readAll() {
        return estudianteRepository.findAll(Sort.by(Sort.Direction.ASC, "apellidos", "nombres")).stream()
                .map(EstudianteMapper::toResponse)
                .toList();
    }

    private void validarUnicidad(String codigo, String dni, Long idActual) {
        boolean codigoDuplicado = idActual == null
                ? estudianteRepository.existsByCodigo(codigo)
                : estudianteRepository.existsByCodigoAndIdNot(codigo, idActual);
        if (codigoDuplicado) {
            log.warn("Código de estudiante duplicado: {}", codigo);
            throw new ReglaNegocioException("Ya existe un estudiante con el código indicado");
        }

        boolean dniDuplicado = idActual == null
                ? estudianteRepository.existsByDni(dni)
                : estudianteRepository.existsByDniAndIdNot(dni, idActual);
        if (dniDuplicado) {
            log.warn("DNI de estudiante duplicado: {}", dni);
            throw new ReglaNegocioException("Ya existe un estudiante con el DNI indicado");
        }
    }

    private Estudiante buscarEntidad(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con id " + id));
    }

    private Carrera buscarCarrera(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con id " + id));
    }

}
