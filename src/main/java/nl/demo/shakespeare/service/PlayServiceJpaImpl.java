package nl.demo.shakespeare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.dto.PlayCreateDto;
import nl.demo.shakespeare.dto.PlayDto;
import nl.demo.shakespeare.dto.PlayUpdateDto;
import nl.demo.shakespeare.mapper.PlayMapper;
import nl.demo.shakespeare.model.Play;
import nl.demo.shakespeare.repository.PlayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayServiceJpaImpl implements PlayServiceJpa {

    private final PlayRepository playRepository;
    private final PlayMapper mapper;


    @Override
    public PlayDto createPlay(PlayCreateDto dto) {
        Play play = mapper.toEntity(dto);
        Play saved = playRepository.save(play);
        return mapper.toDto(saved);
    }

    @Override
    public PlayDto updatePlay(Long id, PlayUpdateDto dto) {
        // Controleer dat path-id en body-id overeenkomen
        if (!id.equals(dto.getId())) {
            throw new IllegalArgumentException("Path ID and body ID do not match");
        }

        Play play = playRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Play not found"));

        // Update de entity via de mapper
        mapper.updateEntityFromDto(dto, play);

        Play saved = playRepository.save(play);
        return mapper.toDto(saved);
    }

    @Override
    public void deletePlay(Long id) {
        playRepository.deleteById(id);
    }


    @Override
    public List<PlayDto> getAllPlays() {
        return playRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlayDto getById(Long id) {
        Play play = playRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Play not found"));
        return mapper.toDto(play);
    }


    @Transactional(readOnly = true)
    @Override
    public PlayDto getPlay(Long id) {
        return playRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Play not found"));
    }

    @Override
    public List<PlayDto> searchPlays(String keyword) {
        return playRepository.searchByText(keyword)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


}
