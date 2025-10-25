package nl.demo.shakespeare.service;

import nl.demo.shakespeare.dto.PlayDto;

import java.util.List;

public interface PlayService {
    List<PlayDto> searchPlays(String keyword);
}
