package pe.edu.upeu.MatriculaBackend.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.MatriculaBackend.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long>, JpaSpecificationExecutor<Curso> {

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    boolean existsByCarreraId(Long carreraId);

    @EntityGraph(attributePaths = "carrera")
    List<Curso> findByCarreraId(Long carreraId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Curso c join fetch c.carrera where c.id = :id")
    Optional<Curso> findByIdForUpdate(@Param("id") Long id);
}
