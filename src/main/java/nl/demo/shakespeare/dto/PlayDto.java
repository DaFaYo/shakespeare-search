package nl.demo.shakespeare.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayDto {
    private String title;
    private String text;
    private String highlightedText; // nieuw veld voor gemarkeerde snippets
}
