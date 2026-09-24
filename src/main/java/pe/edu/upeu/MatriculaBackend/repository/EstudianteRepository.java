package pe.edu.upeu.MatriculaBackend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);

    boolean existsByCarreraId(Long carreraId);

    @EntityGraph(attributePaths = "carrera")
    List<Estudiante> findByCarreraId(Long carreraId);

    @EntityGraph(attributePaths = "carrera")
    Optional<Estudiante> findOneById(Long id);
}
