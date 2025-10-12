package nl.demo.shakespeare.service;

import nl.demo.shakespeare.model.SearchResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ShakespeareSearchService {

    List<SearchResult> search(String keyword) throws IOException;

    List<SearchResult> fuzzySearch(String keyword) throws IOException;

    Map<String, Long> countKeywordOccurrences(String keyword);
}
