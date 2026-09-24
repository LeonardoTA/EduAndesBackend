package pe.edu.upeu.MatriculaBackend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RequestDtoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void crearValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void cerrarValidator() {
        validatorFactory.close();
    }

    @Test
    void cursoInvalidoReportaLosCuatroCamposDelCp03() {
        CursoRequestDTO request = new CursoRequestDTO(
                "is-40", "Curso inválido", 0, 11, -1, true, 1L);

        Set<String> campos = validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertEquals(Set.of("codigo", "creditos", "ciclo", "vacantes"), campos);
    }

    @Test
    void estudianteRechazaCodigoDniYCorreoConFormatoInvalido() {
        EstudianteRequestDTO request = new EstudianteRequestDTO(
                "123", "ABC", "Ana", "Quispe", "correo-invalido", true, 1L);

        Set<String> campos = validator.validate(request).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());

        assertTrue(campos.containsAll(Set.of("codigo", "dni", "email")));
    }

    @Test
    void carreraRechazaNombreCompuestoSoloPorEspacios() {
        CarreraRequestDTO request = new CarreraRequestDTO("   ", "Descripción", true);

        Set<ConstraintViolation<CarreraRequestDTO>> violaciones = validator.validate(request);

        assertTrue(violaciones.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
    }
}
