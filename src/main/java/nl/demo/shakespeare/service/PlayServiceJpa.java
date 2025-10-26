package nl.demo.shakespeare.service;

import nl.demo.shakespeare.dto.PlayDto;

import java.util.List;

public interface PlayServiceJpa {
    List<PlayDto> searchPlays(String keyword);
    PlayDto createPlay(PlayDto dto);
    PlayDto updatePlay(Long id, PlayDto dto);
    void deletePlay(Long id);
    List<PlayDto> getAllPlays();
    PlayDto getById(Long id);
}
