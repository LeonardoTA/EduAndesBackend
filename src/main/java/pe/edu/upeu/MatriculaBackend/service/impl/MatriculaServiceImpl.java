package pe.edu.upeu.MatriculaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.entity.DetalleMatricula;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.MatriculaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MatriculaServiceImpl implements MatriculaService {

    private static final Logger log = LoggerFactory.getLogger(MatriculaServiceImpl.class);
    private static final int MAXIMO_CREDITOS = 20;

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final BigDecimal costoCredito;

    public MatriculaServiceImpl(
            MatriculaRepository matriculaRepository,
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository,
            @Value("${matricula.costo-credito}") BigDecimal costoCredito) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
        this.costoCredito = costoCredito.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public MatriculaResponseDTO create(MatriculaRequestDTO request) {
        Estudiante estudiante = estudianteRepository.findOneById(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id " + request.getEstudianteId()));

        validarEstudianteActivo(estudiante);
        validarMatriculaUnica(estudiante.getId(), request.getPeriodo());

        List<Long> cursoIds = extraerCursoIds(request.getDetalles());
        Map<Long, Curso> cursos = cargarCursosBloqueados(cursoIds);
        int totalCreditos = validarCursosYCalcularCreditos(estudiante, cursoIds, cursos);
        validarLimiteCreditos(totalCreditos);

        Matricula matricula = new Matricula();
        matricula.setPeriodo(request.getPeriodo());
        matricula.setEstudiante(estudiante);
        matricula.setEstado(EstadoMatricula.REGISTRADA);
        matricula.setTotalCreditos(totalCreditos);

        BigDecimal montoTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (Long cursoId : cursoIds) {
            Curso curso = cursos.get(cursoId);
            BigDecimal costo = calcularCosto(curso.getCreditos());

            DetalleMatricula detalle = new DetalleMatricula();
            detalle.setCurso(curso);
            detalle.setCreditos(curso.getCreditos());
            detalle.setCosto(costo);
            matricula.agregarDetalle(detalle);

            curso.setVacantes(curso.getVacantes() - 1);
            montoTotal = montoTotal.add(costo);
        }
        matricula.setMontoTotal(montoTotal.setScale(2, RoundingMode.HALF_UP));

        Matricula guardada = matriculaRepository.save(matricula);
        log.info(
                "Matricula registrada con id {} para el estudiante {} en el periodo {}",
                guardada.getId(),
                estudiante.getId(),
                request.getPeriodo());
        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public MatriculaResponseDTO read(Long id) {
        return toResponse(buscarConDetalles(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> readAll() {
        return matriculaRepository.findAllConDetalles().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MatriculaResponseDTO anular(Long id) {
        Matricula matricula = matriculaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matricula no encontrada con id " + id));

        if (matricula.getEstado() == EstadoMatricula.ANULADA) {
            log.warn("No se puede anular nuevamente la matricula {}", id);
            throw new ReglaNegocioException("La matricula ya se encuentra anulada");
        }

        List<DetalleMatricula> detalles = new ArrayList<>(matricula.getDetalles());
        List<Long> cursoIds = detalles.stream()
                .map(detalle -> detalle.getCurso().getId())
                .distinct()
                .sorted()
                .toList();
        Map<Long, Curso> cursos = cargarCursosBloqueados(cursoIds);

        for (DetalleMatricula detalle : detalles) {
            Curso curso = cursos.get(detalle.getCurso().getId());
            curso.setVacantes(curso.getVacantes() + 1);
        }
        matricula.setEstado(EstadoMatricula.ANULADA);
        Matricula anulada = matriculaRepository.saveAndFlush(matricula);

        log.info("Matricula {} anulada; se devolvieron {} vacantes", id, detalles.size());
        return toResponse(anulada);
    }

    private void validarEstudianteActivo(Estudiante estudiante) {
        if (!Boolean.TRUE.equals(estudiante.getEstado())) {
            log.warn("RN-01: intento de matricula para el estudiante inactivo {}", estudiante.getId());
            throw new ReglaNegocioException("Solo se puede matricular un estudiante activo");
        }
    }

    private void validarMatriculaUnica(Long estudianteId, String periodo) {
        if (matriculaRepository.existsByEstudianteIdAndPeriodoAndEstado(
                estudianteId,
                periodo,
                EstadoMatricula.REGISTRADA)) {
            log.warn(
                    "RN-03: el estudiante {} ya tiene una matricula registrada en el periodo {}",
                    estudianteId,
                    periodo);
            throw new ReglaNegocioException(
                    "El estudiante ya tiene una matricula registrada en el periodo indicado");
        }
    }

    private List<Long> extraerCursoIds(List<DetalleMatriculaRequestDTO> detalles) {
        List<Long> ids = detalles.stream()
                .map(DetalleMatriculaRequestDTO::getCursoId)
                .toList();
        Set<Long> idsUnicos = new LinkedHashSet<>(ids);
        if (idsUnicos.size() != ids.size()) {
            log.warn("No se puede registrar una matricula con cursos repetidos");
            throw new ReglaNegocioException("No se puede incluir un curso mas de una vez en la matricula");
        }
        return new ArrayList<>(idsUnicos);
    }

    private Map<Long, Curso> cargarCursosBloqueados(List<Long> cursoIds) {
        Map<Long, Curso> cursos = new HashMap<>();
        cursoIds.stream().sorted().forEach(cursoId -> {
            Curso curso = cursoRepository.findByIdForUpdate(cursoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Curso no encontrado con id " + cursoId));
            cursos.put(cursoId, curso);
        });
        return cursos;
    }

    private int validarCursosYCalcularCreditos(
            Estudiante estudiante,
            List<Long> cursoIds,
            Map<Long, Curso> cursos) {
        int totalCreditos = 0;
        for (Long cursoId : cursoIds) {
            Curso curso = cursos.get(cursoId);
            if (!Boolean.TRUE.equals(curso.getEstado())) {
                log.warn("RN-01: intento de matricula en el curso inactivo {}", cursoId);
                throw new ReglaNegocioException("Solo se puede matricular en cursos activos");
            }
            if (!curso.getCarrera().getId().equals(estudiante.getCarrera().getId())) {
                log.warn(
                        "RN-01: el curso {} no pertenece a la carrera del estudiante {}",
                        cursoId,
                        estudiante.getId());
                throw new ReglaNegocioException(
                        "Todos los cursos deben pertenecer a la carrera del estudiante");
            }
            if (curso.getVacantes() <= 0) {
                log.warn("RN-02: el curso {} no tiene vacantes", cursoId);
                throw new ReglaNegocioException("El curso " + curso.getCodigo() + " no tiene vacantes disponibles");
            }
            totalCreditos += curso.getCreditos();
        }
        return totalCreditos;
    }

    private void validarLimiteCreditos(int totalCreditos) {
        if (totalCreditos > MAXIMO_CREDITOS) {
            log.warn("RN-04: la matricula suma {} creditos y supera el maximo de {}", totalCreditos, MAXIMO_CREDITOS);
            throw new ReglaNegocioException("La matricula no puede superar 20 creditos");
        }
    }

    private BigDecimal calcularCosto(Integer creditos) {
        return costoCredito
                .multiply(BigDecimal.valueOf(creditos.longValue()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private Matricula buscarConDetalles(Long id) {
        return matriculaRepository.findOneById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matricula no encontrada con id " + id));
    }

    private MatriculaResponseDTO toResponse(Matricula matricula) {
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
                .map(this::toDetalleResponse)
                .toList());
        response.setFechaCreacion(matricula.getFechaCreacion());
        response.setFechaModificacion(matricula.getFechaModificacion());
        return response;
    }

    private DetalleMatriculaResponseDTO toDetalleResponse(DetalleMatricula detalle) {
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
