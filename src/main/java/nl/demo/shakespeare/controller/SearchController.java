package nl.demo.shakespeare.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.model.SearchResult;
import nl.demo.shakespeare.service.ShakespeareSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final ShakespeareSearchService searchService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        List<SearchResult> results;
        try {
            results = searchService.search(q);
        } catch (Exception e) {
            log.error("Error in search controller for query '{}'", q, e);
            results = Collections.emptyList();
            model.addAttribute("errorMessage", "Search failed: " + e.getMessage());
        }
        model.addAttribute("query", q);
        model.addAttribute("results", results);
        return "results";
    }

    @GetMapping("/api/keyword-count")
    @ResponseBody
    public Map<String, Long> countKeyword(@RequestParam("q") String keyword) {
        return searchService.countKeywordOccurrences(keyword);
    }

}
