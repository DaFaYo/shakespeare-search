package nl.demo.shakespeare.service;

import nl.demo.shakespeare.dto.PlayCreateDto;
import nl.demo.shakespeare.dto.PlayDto;
import nl.demo.shakespeare.dto.PlayUpdateDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PlayServiceJpa {
    @Transactional(readOnly = true)
    PlayDto getPlay(Long id);
    List<PlayDto> searchPlays(String keyword);
    PlayDto createPlay(PlayCreateDto dto);
    PlayDto updatePlay(Long id, PlayUpdateDto dto);
    void deletePlay(Long id);
    List<PlayDto> getAllPlays();
    PlayDto getById(Long id);
}
