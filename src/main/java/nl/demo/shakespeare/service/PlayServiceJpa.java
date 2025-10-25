package nl.demo.shakespeare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.dto.PlayDto;
import nl.demo.shakespeare.model.Play;
import nl.demo.shakespeare.repository.PlayRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayServiceJpa implements PlayService {

    private final PlayRepository playRepository;

    @Override
    public List<PlayDto> searchPlays(String keyword) {
        log.info("Zoekopdracht ontvangen voor keyword: '{}'", keyword);

        List<Play> plays = playRepository.findByTextContainingIgnoreCase(keyword);

        if (plays.isEmpty()) {
            log.warn("Geen resultaten gevonden voor keyword: '{}'", keyword);
        } else {
            log.debug("Aantal resultaten gevonden voor '{}': {}", keyword, plays.size());
        }

        List<PlayDto> result = plays.stream()
                .map(play -> PlayDto.builder()
                        .title(play.getTitle())
                        .text(play.getText())
                        .build())
                .collect(Collectors.toList());

        log.info("Zoekopdracht afgerond voor keyword: '{}'", keyword);
        return result;
    }
}
