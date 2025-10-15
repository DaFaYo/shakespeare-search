package nl.demo.shakespeare.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.model.SearchResult;
import nl.demo.shakespeare.model.ShakespeareDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShakespeareSearchServiceImpl implements ShakespeareSearchService {

    private final ElasticsearchClient client;

    @Override
    public List<SearchResult> search(String keyword, boolean fuzzy, boolean exact) {
        log.info("Executing search for keyword: '{}', fuzzy={}, exact={}", keyword, fuzzy, exact);

        try {
            SearchResponse<ShakespeareDocument> response = client.search(s -> s
                            .index("shakespeare")
                            .query(q -> {
                                if (exact) {
                                    return q.matchPhrase(mp -> mp.field("text").query(keyword));
                                } else if (fuzzy) {
                                    return q.fuzzy(fz -> fz.field("text").value(keyword).fuzziness("AUTO"));
                                } else {
                                    return q.match(m -> m.field("text").query(keyword));
                                }
                            })
                            .highlight(h -> h
                                    .fields("text", f -> f
                                            .preTags("<mark>")
                                            .postTags("</mark>")
                                            .fragmentSize(150)
                                            .numberOfFragments(3)
                                    )
                            )
                            .size(1000),
                    ShakespeareDocument.class
            );

            List<Hit<ShakespeareDocument>> hits = response.hits().hits();
            Map<String, Long> counts = countKeywordOccurrences(keyword);

            return hits.stream()
                    .map(hit -> mapHitToSearchResult(hit, counts))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("IOException during search", e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Unexpected error during search", e);
            return Collections.emptyList();
        }
    }

    private SearchResult mapHitToSearchResult(Hit<ShakespeareDocument> hit, Map<String, Long> counts) {
        assert hit.source() != null;
        String title = hit.source().getTitle();
        long occurrences = counts.getOrDefault(title, 0L);

        List<String> snippets;
        if (hit.highlight() != null && hit.highlight().containsKey("text")) {
            snippets = hit.highlight().get("text"); // behoud de losse fragments
        } else {
            snippets = List.of(hit.source().getText());
        }

        return new SearchResult(title, snippets, occurrences);
    }
    
    /**
     * Telt het aantal keer dat het keyword voorkomt in elk document.
     */
    @Override
    public Map<String, Long> countKeywordOccurrences(String keyword) {
        Map<String, Long> counts = new HashMap<>();
        try {
            SearchResponse<ShakespeareDocument> response = client.search(s -> s
                            .index("shakespeare")
                            .query(q -> q.match(m -> m.field("text").query(keyword)))
                            .size(1000),
                    ShakespeareDocument.class
            );

            response.hits().hits().forEach(hit -> {
                assert hit.source() != null;
                String title = hit.source().getTitle();
                long occurrence = countKeywordInText(hit.source().getText(), keyword);
                counts.put(title, occurrence);
            });

        } catch (IOException e) {
            log.error("Fout bij zoeken in Elasticsearch", e);
        }
        return counts;
    }

    private long countKeywordInText(String text, String keyword) {
        if (text == null || keyword == null || keyword.isBlank()) return 0;

        int count = 0;
        String lowerText = text.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        int index = lowerText.indexOf(lowerKeyword);
        while (index != -1) {
            count++;
            index = lowerText.indexOf(lowerKeyword, index + lowerKeyword.length());
        }
        return count;
    }
}
