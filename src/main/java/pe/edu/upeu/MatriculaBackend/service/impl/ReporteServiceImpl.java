package pe.edu.upeu.MatriculaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.reporte.MatriculadosPorCursoDTO;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.ReporteService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);

    private final MatriculaRepository matriculaRepository;

    public ReporteServiceImpl(MatriculaRepository matriculaRepository) {
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculadosPorCursoDTO> matriculadosPorCurso(String periodo, Long carreraId) {
        List<MatriculadosPorCursoDTO> reporte = matriculaRepository
                .reporteMatriculadosPorCurso(periodo, carreraId)
                .stream()
                .map(fila -> new MatriculadosPorCursoDTO(
                        fila.getCodigo(),
                        fila.getCurso(),
                        fila.getMatriculados(),
                        escalaMonetaria(fila.getMontoRecaudado())))
                .toList();
        log.info(
                "Reporte de matriculados por curso generado para el periodo {} y carrera {}: {} filas",
                periodo,
                carreraId,
                reporte.size());
        return reporte;
    }

    private BigDecimal escalaMonetaria(BigDecimal monto) {
        return monto == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : monto.setScale(2, RoundingMode.HALF_UP);
    }
}
