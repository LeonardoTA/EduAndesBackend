package pe.edu.upeu.MatriculaBackend.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.CursoRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;

@Service
public class CursoServiceImpl implements CursoService {

    private static final Set<String> CAMPOS_ORDENABLES = Set.of("nombre", "creditos", "vacantes");
    private static final Logger log = LoggerFactory.getLogger(CursoServiceImpl.class);

    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;
    private final MatriculaRepository matriculaRepository;

    public CursoServiceImpl(
            CursoRepository cursoRepository,
            CarreraRepository carreraRepository,
            MatriculaRepository matriculaRepository) {
        this.cursoRepository = cursoRepository;
        this.carreraRepository = carreraRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    @Transactional
    public CursoResponseDTO create(CursoRequestDTO request) {
        String codigo = request.getCodigo().trim();
        validarCodigoDisponible(codigo, null);
        Carrera carrera = buscarCarrera(request.getCarreraId());

        Curso curso = new Curso();
        copiarDatos(request, curso, carrera, codigo);
        Curso guardado = cursoRepository.save(curso);
        log.info("Curso creado con id {}", guardado.getId());
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public CursoResponseDTO update(Long id, CursoRequestDTO request) {
        Curso curso = buscarEntidad(id);
        String codigo = request.getCodigo().trim();
        validarCodigoDisponible(codigo, id);
        Carrera carrera = buscarCarrera(request.getCarreraId());

        copiarDatos(request, curso, carrera, codigo);
        Curso actualizado = cursoRepository.save(curso);
        log.info("Curso actualizado con id {}", id);
        return toResponse(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO read(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Curso curso = buscarEntidad(id);
        if (matriculaRepository.existsByDetallesCursoId(id)) {
            log.warn("No se puede eliminar el curso {} porque tiene matrículas asociadas", id);
            throw new ReglaNegocioException("No se puede eliminar el curso porque tiene matrículas asociadas");
        }
        cursoRepository.delete(curso);
        log.info("Curso eliminado con id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> readAll() {
        return cursoRepository.findAll(Sort.by(Sort.Direction.ASC, "nombre")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> findByCarreraId(Long carreraId) {
        buscarCarrera(carreraId);
        return cursoRepository.findByCarreraId(carreraId).stream()
                .sorted(Comparator.comparing(Curso::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> buscar(
            String nombre,
            Long carreraId,
            Integer ciclo,
            Boolean conVacantes,
            String orden,
            String direccion) {
        String campoOrden = normalizarOrden(orden);
        Sort.Direction sentido = normalizarDireccion(direccion);

        Specification<Curso> filtros = (root, query, cb) -> cb.conjunction();
        if (nombre != null && !nombre.isBlank()) {
            String patron = "%" + nombre.trim().toLowerCase(Locale.ROOT) + "%";
            filtros = filtros.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), patron));
        }
        if (carreraId != null) {
            filtros = filtros.and((root, query, cb) -> cb.equal(root.get("carrera").get("id"), carreraId));
        }
        if (ciclo != null) {
            filtros = filtros.and((root, query, cb) -> cb.equal(root.get("ciclo"), ciclo));
        }
        if (conVacantes != null) {
            filtros = conVacantes
                    ? filtros.and((root, query, cb) -> cb.greaterThan(root.get("vacantes"), 0))
                    : filtros.and((root, query, cb) -> cb.equal(root.get("vacantes"), 0));
        }

        return cursoRepository.findAll(filtros, Sort.by(sentido, campoOrden)).stream()
                .map(this::toResponse)
                .toList();
    }

    private String normalizarOrden(String orden) {
        String valor = orden == null || orden.isBlank() ? "nombre" : orden.trim().toLowerCase(Locale.ROOT);
        if (!CAMPOS_ORDENABLES.contains(valor)) {
            throw new ReglaNegocioException("Campo de orden inválido. Use nombre, creditos o vacantes");
        }
        return valor;
    }

    private Sort.Direction normalizarDireccion(String direccion) {
        String valor = direccion == null || direccion.isBlank()
                ? "asc"
                : direccion.trim().toLowerCase(Locale.ROOT);
        if (!valor.equals("asc") && !valor.equals("desc")) {
            throw new ReglaNegocioException("Dirección de orden inválida. Use asc o desc");
        }
        return valor.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    private void validarCodigoDisponible(String codigo, Long idActual) {
        boolean duplicado = idActual == null
                ? cursoRepository.existsByCodigoIgnoreCase(codigo)
                : cursoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, idActual);
        if (duplicado) {
            log.warn("Código de curso duplicado: {}", codigo);
            throw new ReglaNegocioException("Ya existe un curso con el código indicado");
        }
    }

    private Curso buscarEntidad(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Curso no encontrado con id " + id));
    }

    private Carrera buscarCarrera(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con id " + id));
    }

    private void copiarDatos(CursoRequestDTO request, Curso curso, Carrera carrera, String codigo) {
        curso.setCodigo(codigo);
        curso.setNombre(request.getNombre().trim());
        curso.setCreditos(request.getCreditos());
        curso.setCiclo(request.getCiclo());
        curso.setVacantes(request.getVacantes());
        curso.setEstado(request.getEstado());
        curso.setCarrera(carrera);
    }

    private CursoResponseDTO toResponse(Curso curso) {
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
