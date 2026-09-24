package pe.edu.upeu.MatriculaBackend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByEstudianteIdAndPeriodoAndEstado(
            Long estudianteId,
            String periodo,
            EstadoMatricula estado);

    boolean existsByDetallesCursoId(Long cursoId);

    @EntityGraph(attributePaths = {"estudiante", "detalles", "detalles.curso"})
    Optional<Matricula> findOneById(Long id);

    @EntityGraph(attributePaths = {"estudiante", "detalles", "detalles.curso"})
    List<Matricula> findByPeriodo(String periodo);

    @Query("""
            select c.codigo as codigo,
                   c.nombre as curso,
                   count(d.id) as matriculados,
                   sum(d.costo) as montoRecaudado
              from DetalleMatricula d
              join d.matricula m
              join d.curso c
             where m.estado = :estado
               and m.periodo = :periodo
               and (:carreraId is null or c.carrera.id = :carreraId)
             group by c.codigo, c.nombre
             order by c.nombre asc
            """)
    List<MatriculadosPorCursoProjection> findReporteMatriculadosPorCurso(
            @Param("periodo") String periodo,
            @Param("carreraId") Long carreraId,
            @Param("estado") EstadoMatricula estado);

    default List<MatriculadosPorCursoProjection> reporteMatriculadosPorCurso(
            String periodo,
            Long carreraId) {
        return findReporteMatriculadosPorCurso(periodo, carreraId, EstadoMatricula.REGISTRADA);
    }

    interface MatriculadosPorCursoProjection {

        String getCodigo();

        String getCurso();

        Long getMatriculados();

        BigDecimal getMontoRecaudado();
    }
}
