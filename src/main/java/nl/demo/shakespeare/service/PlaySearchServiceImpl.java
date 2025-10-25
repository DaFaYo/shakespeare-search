package nl.demo.shakespeare.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.model.PlayDocument;
import nl.demo.shakespeare.model.SearchResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaySearchServiceImpl implements PlaySearchService {

    private final ElasticsearchClient client;

    @Override
    public List<SearchResult> searchPlays(String keyword, boolean fuzzy, boolean exact) {
        log.info("Executing play search for keyword: '{}', fuzzy={}, exact={}", keyword, fuzzy, exact);

        try {
            SearchResponse<PlayDocument> response = client.search(s -> s
                            .index("plays") // dit is de index waarin logstash de MySQL data zet
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
                    PlayDocument.class
            );

            List<Hit<PlayDocument>> hits = response.hits().hits();
            Map<String, Long> counts = countKeywordOccurrences(keyword);

            return hits.stream()
                    .map(hit -> mapHitToSearchResult(hit, counts))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("IOException during play search", e);
            return Collections.emptyList();
        }
    }

    private SearchResult mapHitToSearchResult(Hit<PlayDocument> hit, Map<String, Long> counts) {
        assert hit.source() != null;
        String title = hit.source().getTitle();
        long occurrences = counts.getOrDefault(title, 0L);

        List<String> snippets;
        if (hit.highlight() != null && hit.highlight().containsKey("text")) {
            snippets = hit.highlight().get("text");
        } else {
            snippets = List.of(hit.source().getText());
        }

        return new SearchResult(title, snippets, occurrences);
    }

    public Map<String, Long> countKeywordOccurrences(String keyword) {
        Map<String, Long> counts = new HashMap<>();
        try {
            SearchResponse<PlayDocument> response = client.search(s -> s
                            .index("plays")
                            .query(q -> q.match(m -> m.field("text").query(keyword)))
                            .size(1000),
                    PlayDocument.class
            );

            response.hits().hits().forEach(hit -> {
                assert hit.source() != null;
                counts.put(hit.source().getTitle(),
                        countKeywordInText(hit.source().getText(), keyword));
            });

        } catch (IOException e) {
            log.error("Error counting occurrences in plays", e);
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