package pe.edu.upeu.MatriculaBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
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

    @Query("""
            select distinct m
              from Matricula m
              join fetch m.estudiante
              left join fetch m.detalles d
              left join fetch d.curso
             where m.id = :id
            """)
    Optional<Matricula> findOneById(@Param("id") Long id);

    @Query("""
            select distinct m
              from Matricula m
              join fetch m.estudiante
              left join fetch m.detalles d
              left join fetch d.curso
             order by m.fecha desc, m.id desc
            """)
    List<Matricula> findAllConDetalles();

    @Query("""
            select distinct m
              from Matricula m
              join fetch m.estudiante
              left join fetch m.detalles d
              left join fetch d.curso
             where m.periodo = :periodo
             order by m.fecha desc, m.id desc
            """)
    List<Matricula> findByPeriodo(@Param("periodo") String periodo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Matricula m where m.id = :id")
    Optional<Matricula> findByIdForUpdate(@Param("id") Long id);

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
             order by c.codigo asc
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
