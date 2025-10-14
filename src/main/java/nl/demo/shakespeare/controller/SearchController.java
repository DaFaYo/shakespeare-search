package nl.demo.shakespeare.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.demo.shakespeare.model.SearchResult;
import nl.demo.shakespeare.service.ShakespeareSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public String search(@RequestParam("q") String query,
                         @RequestParam(value = "fuzzy", required = false) Boolean fuzzy,
                         Model model,
                         HttpServletRequest request) {

        List<SearchResult> results;
        try {
            if (Boolean.TRUE.equals(fuzzy)) {
                results = searchService.fuzzySearch(query);
            } else {
                results = searchService.search(query);
            }
        } catch (Exception e) {
            log.error("Search failed", e);
            results = Collections.emptyList();
        }

        model.addAttribute("query", query);
        model.addAttribute("results", results);
        model.addAttribute("fuzzy", fuzzy != null && fuzzy);

        // Controleer of het een AJAX-request is → retourneer fragment
        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "fragments :: results"; // Thymeleaf fragment
        }

        return "index"; // normale pagina
    }




    @GetMapping("/fuzzy-search")
    public String fuzzySearch(@RequestParam String q, Model model) {
        List<SearchResult> results;
        try {
            results = searchService.fuzzySearch(q);
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
