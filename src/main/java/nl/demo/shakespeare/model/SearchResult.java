package nl.demo.shakespeare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String title;
    private List<String> highlightedTextSnippets; // Meerdere highlight-fragments
    private long occurrences;
}
