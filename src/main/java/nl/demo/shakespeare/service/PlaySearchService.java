package nl.demo.shakespeare.service;

import nl.demo.shakespeare.model.SearchResult;

import java.util.List;
import java.util.Map;

public interface PlaySearchService {
    List<SearchResult> searchPlays(String keyword, boolean fuzzy, boolean exact);
    Map<String, Long> countKeywordOccurrences(String keyword);
}
