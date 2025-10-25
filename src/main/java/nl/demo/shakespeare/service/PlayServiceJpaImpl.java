package nl.demo.shakespeare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.dto.PlayDto;
import nl.demo.shakespeare.model.Play;
import nl.demo.shakespeare.repository.PlayRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayServiceJpaImpl implements PlayServiceJpa {

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

        return plays.stream()
                .map(play -> {
                    String highlightedText = highlightKeyword(play.getText(), keyword);
                    return PlayDto.builder()
                            .title(play.getTitle())
                            .text(play.getText())
                            .highlightedText(highlightedText)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Voeg <mark> rond de gevonden zoekterm toe.
     * Case-insensitive.
     */
    private String highlightKeyword(String text, String keyword) {
        if (text == null || keyword == null || keyword.isBlank()) return text;

        // Escape regex speciale tekens in keyword
        String escapedKeyword = Pattern.quote(keyword);

        // Case-insensitive highlight
        return text.replaceAll("(?i)" + escapedKeyword, "<mark>$0</mark>");
    }

}
