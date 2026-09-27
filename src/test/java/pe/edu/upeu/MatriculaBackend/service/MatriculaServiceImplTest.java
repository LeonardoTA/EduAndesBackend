package pe.edu.upeu.MatriculaBackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.impl.MatriculaServiceImpl;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceImplTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private CursoRepository cursoRepository;

    private MatriculaServiceImpl service;
    private Carrera carrera;
    private Estudiante estudiante;

    @BeforeEach
    void preparar() {
        service = new MatriculaServiceImpl(
                matriculaRepository, estudianteRepository, cursoRepository, new BigDecimal("120.00"));
        carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Ingeniería de Sistemas");
        estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setCodigo("202600001");
        estudiante.setNombres("Ana");
        estudiante.setApellidos("Quispe");
        estudiante.setEstado(true);
        estudiante.setCarrera(carrera);
    }

    @Test
    void rechazaCursoSinVacantesSinGuardarMatriculaNiDescontarVacantes() {
        Curso curso = curso(10L, 3, 0);
        when(estudianteRepository.findOneById(1L)).thenReturn(Optional.of(estudiante));
        when(cursoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(curso));

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> service.create(request(10L)));

        assertEquals("El curso IS401 no tiene vacantes disponibles", error.getMessage());
        assertEquals(0, curso.getVacantes());
        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    @Test
    void registraDetalleCalculaMontoYDescuentaUnaVacantePorCurso() {
        Curso primerCurso = curso(10L, 3, 5);
        Curso segundoCurso = curso(11L, 4, 8);
        segundoCurso.setCodigo("IS402");
        when(estudianteRepository.findOneById(1L)).thenReturn(Optional.of(estudiante));
        when(cursoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(primerCurso));
        when(cursoRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(segundoCurso));
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(invocation -> {
            Matricula matricula = invocation.getArgument(0);
            matricula.setId(20L);
            return matricula;
        });

        MatriculaResponseDTO response = service.create(request(10L, 11L));

        assertEquals(EstadoMatricula.REGISTRADA, response.getEstado());
        assertEquals(7, response.getTotalCreditos());
        assertEquals(new BigDecimal("840.00"), response.getMontoTotal());
        assertEquals(List.of(new BigDecimal("360.00"), new BigDecimal("480.00")),
                response.getDetalles().stream().map(detalle -> detalle.getCosto()).toList());
        assertEquals(4, primerCurso.getVacantes());
        assertEquals(7, segundoCurso.getVacantes());
        verify(matriculaRepository).save(any(Matricula.class));
    }

    private Curso curso(Long id, int creditos, int vacantes) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setCodigo("IS401");
        curso.setNombre("Curso " + id);
        curso.setCreditos(creditos);
        curso.setVacantes(vacantes);
        curso.setEstado(true);
        curso.setCarrera(carrera);
        return curso;
    }

    private MatriculaRequestDTO request(Long... cursoIds) {
        return new MatriculaRequestDTO(1L, "2026-2",
                java.util.Arrays.stream(cursoIds)
                        .map(DetalleMatriculaRequestDTO::new)
                        .toList());
    }
}
