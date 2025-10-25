package nl.demo.shakespeare.controller;

import lombok.RequiredArgsConstructor;
import nl.demo.shakespeare.model.SearchResult;
import nl.demo.shakespeare.service.PlaySearchService;
import nl.demo.shakespeare.service.ShakespeareSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShakespeareSearchRestController {

    private final ShakespeareSearchService searchService;
    private final PlaySearchService playSearchService;

    @GetMapping("/documents/search")
    public List<SearchResult> searchJson(@RequestParam("q") String query,
                                         @RequestParam(name = "fuzzy", defaultValue = "false") boolean fuzzy,
                                         @RequestParam(name = "exact", defaultValue = "false") boolean exact) {
        return searchService.search(query, fuzzy, exact);
    }

    @GetMapping("/database/search")
    public List<SearchResult> searchPlays(@RequestParam String q,
                                          @RequestParam(name="fuzzy", defaultValue="false") boolean fuzzy,
                                          @RequestParam(name="exact", defaultValue="false") boolean exact) {
        return playSearchService.searchPlays(q, fuzzy, exact);
    }
}
