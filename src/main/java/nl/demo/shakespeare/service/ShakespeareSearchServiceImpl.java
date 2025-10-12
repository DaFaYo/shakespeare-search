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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShakespeareSearchServiceImpl implements ShakespeareSearchService {

    private final ElasticsearchClient client;

    @Override
    public List<SearchResult> search(String keyword) {
        return executeSearch(keyword, exactMatchQuery(keyword));
    }

    @Override
    public List<SearchResult> fuzzySearch(String keyword) {
        return executeSearch(keyword, fuzzyQuery(keyword));
    }


    /**
     * Telt het aantal keer dat het keyword voorkomt in elk document.
     * Simpele en werkende aanpak: zoekmatching en lokale telling.
     */
    @Override
    public Map<String, Long> countKeywordOccurrences(String keyword) {
        Map<String, Long> counts = new HashMap<>();
        try {
            SearchResponse<ShakespeareDocument> response = client.search(s -> s
                            .index("shakespeare")
                            .query(q -> q
                                    .match(m -> m
                                            .field("text")
                                            .query(keyword)
                                    )
                            )
                            .size(1000), // max aantal stukken
                    ShakespeareDocument.class
            );

            response.hits().hits().forEach(hit -> {
                assert hit.source() != null;
                String title = hit.source().getTitle();
                String text = hit.source().getText();
                long occurrence = countKeywordInText(text, keyword);
                counts.put(title, occurrence);
            });

        } catch (IOException e) {
            log.error("Fout bij zoeken in Elasticsearch", e);
        }
        return counts;
    }


    /**
     * Algemene zoekmethode die een Query object accepteert.
     */
    private List<SearchResult> executeSearch(String keyword,
                                             co.elastic.clients.elasticsearch._types.query_dsl.Query query) {
        log.info("Executing search for keyword: '{}'", keyword);

        try {
            SearchResponse<ShakespeareDocument> response = client.search(s -> s
                            .index("shakespeare")
                            .query(query)
                            .highlight(h -> h
                                    .fields("text", f -> f
                                            .preTags("<mark>")
                                            .postTags("</mark>")
                                            .fragmentSize(150)
                                            .numberOfFragments(3)
                                    )
                            )
                            .size(50), // max aantal hits
                    ShakespeareDocument.class
            );

            List<Hit<ShakespeareDocument>> hits = response.hits().hits();
            log.info("Found {} hits", hits.size());

            // Zet hits om naar SearchResult
            return hits.stream()
                    .map(hit -> {
                        assert hit.source() != null;
                        String text = hit.source().getText();

                        // Highlighted text
                        String highlighted = hit.highlight() != null && hit.highlight().containsKey("text")
                                ? String.join(" ", hit.highlight().get("text"))
                                : text;

                        String title = hit.source().getTitle();
                        long occurrences = countKeywordInText(text, keyword); // telt altijd in tekst

                        return new SearchResult(title, highlighted, occurrences);
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("IOException during search", e);
        } catch (Exception e) {
            log.error("Unexpected error during search", e);
        }

        return Collections.emptyList();
    }

    /**
     * Bouwt een exact match query.
     */
    private co.elastic.clients.elasticsearch._types.query_dsl.Query exactMatchQuery(String keyword) {
        return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q ->
                q.match(m -> m.field("text").query(keyword))
        );
    }

    /**
     * Bouwt een fuzzy query (typo-tolerantie).
     */
    private co.elastic.clients.elasticsearch._types.query_dsl.Query fuzzyQuery(String keyword) {
        return co.elastic.clients.elasticsearch._types.query_dsl.Query.of(q ->
                q.fuzzy(f -> f.field("text").value(keyword).fuzziness("AUTO"))
        );
    }

    /**
     * Telt het aantal keer dat het keyword voorkomt in de tekst.
     * Case-insensitive.
     */
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
