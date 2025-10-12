package nl.demo.shakespeare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private String title;
    private String highlightedText;
    private long occurrences;
}
