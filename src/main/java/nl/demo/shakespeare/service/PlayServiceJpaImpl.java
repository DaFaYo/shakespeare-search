package nl.demo.shakespeare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.dto.PlayDto;
import nl.demo.shakespeare.model.Play;
import nl.demo.shakespeare.repository.PlayRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayServiceJpaImpl implements PlayServiceJpa {

    private final PlayRepository playRepository;

    @Override
    public List<PlayDto> searchPlays(String keyword) {
        return playRepository.searchByText(keyword)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlayDto createPlay(PlayDto dto) {
        log.info("Adding new play: {}", dto.getTitle());
        Play play = toEntity(dto);
        try {
            return toDto(playRepository.save(play));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Titel '" + dto.getTitle() + "' bestaat al.");
        }
    }

    @Override
    public PlayDto updatePlay(Long id, PlayDto dto) {
        Play play = playRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Play niet gevonden met id " + id));
        play.setTitle(dto.getTitle());
        play.setText(dto.getText());
        return toDto(playRepository.save(play));
    }

    @Override
    public void deletePlay(Long id) {
        playRepository.deleteById(id);
    }

    @Override
    public List<PlayDto> getAllPlays() {
        return playRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlayDto getById(Long id) {
        return playRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Play niet gevonden met id " + id));
    }

    private PlayDto toDto(Play play) {
        return PlayDto.builder()
                .id(play.getId())
                .title(play.getTitle())
                .text(play.getText())
                .build();
    }

    private Play toEntity(PlayDto dto) {
        return Play.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .text(dto.getText())
                .build();
    }
}
