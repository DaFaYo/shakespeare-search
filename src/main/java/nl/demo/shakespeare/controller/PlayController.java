package nl.demo.shakespeare.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.demo.shakespeare.dto.PlayDto;
import nl.demo.shakespeare.service.PlayServiceJpa;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plays")
@RequiredArgsConstructor
@Tag(name = "Plays", description = "CRUD endpoints voor Shakespeare plays")
public class PlayController {

    private final PlayServiceJpa playService;

    @Operation(summary = "Geef alle plays terug")
    @GetMapping
    public List<PlayDto> getAll() {
        return playService.getAllPlays();
    }

    @Operation(summary = "Zoek plays op id")
    @GetMapping("/{id}")
    public PlayDto getById(@PathVariable Long id) {
        return playService.getById(id);
    }

    @Operation(summary = "Maak een nieuwe play aan")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayDto create(@Valid @RequestBody PlayDto dto) {
        return playService.createPlay(dto);
    }

    @Operation(summary = "Update een bestaande play")
    @PutMapping("/{id}")
    public PlayDto update(@PathVariable Long id, @Valid @RequestBody PlayDto dto) {
        return playService.updatePlay(id, dto);
    }

    @Operation(summary = "Verwijder een play")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        playService.deletePlay(id);
    }

    @Operation(summary = "Zoek plays op tekstinhoud")
    @GetMapping("/search")
    public List<PlayDto> search(@RequestParam String q) {
        return playService.searchPlays(q);
    }
}
