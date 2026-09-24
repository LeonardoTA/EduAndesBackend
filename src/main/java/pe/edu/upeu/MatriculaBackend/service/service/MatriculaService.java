package pe.edu.upeu.MatriculaBackend.service.service;

import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;

import java.util.List;

public interface MatriculaService {

    MatriculaResponseDTO create(MatriculaRequestDTO request);

    MatriculaResponseDTO read(Long id);

    List<MatriculaResponseDTO> readAll();

    MatriculaResponseDTO anular(Long id);
}
