package nl.demo.shakespeare.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.demo.shakespeare.dto.PlayCreateDto;
import nl.demo.shakespeare.dto.PlayDto;
import nl.demo.shakespeare.dto.PlayUpdateDto;
import nl.demo.shakespeare.service.PlayServiceJpa;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plays")
@RequiredArgsConstructor
@Tag(name = "Plays", description = "CRUD endpoints voor Shakespeare plays")
public class PlayController {

    private final PlayServiceJpa playService;

    @GetMapping
    @Operation(summary = "Get all plays")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all plays")
    })
    public ResponseEntity<List<PlayDto>> getAll() {
        return ResponseEntity.ok(playService.getAllPlays());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get play by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Play found"),
            @ApiResponse(responseCode = "404", description = "Play not found")
    })
    public ResponseEntity<PlayDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(playService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new play")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Play created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<PlayDto> create(@RequestBody @Valid PlayCreateDto dto) {
        PlayDto created = playService.createPlay(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing play")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Play updated"),
            @ApiResponse(responseCode = "400", description = "Path ID and body ID mismatch"),
            @ApiResponse(responseCode = "404", description = "Play not found")
    })
    public ResponseEntity<PlayDto> update(@PathVariable Long id,
                                          @RequestBody @Valid PlayUpdateDto dto) {
        return ResponseEntity.ok(playService.updatePlay(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a play by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Play deleted"),
            @ApiResponse(responseCode = "404", description = "Play not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playService.deletePlay(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search plays by keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results")
    })
    public ResponseEntity<List<PlayDto>> search(@RequestParam String q) {
        return ResponseEntity.ok(playService.searchPlays(q));
    }
}