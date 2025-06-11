package com.toyverse.toyverse_backend.controller;

import com.toyverse.toyverse_backend.dto.ToyDto;
import com.toyverse.toyverse_backend.dto.ToyRequestDto;
import com.toyverse.toyverse_backend.service.ToyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/toys")
public class ToyController {

    private final ToyService toyService;

    @GetMapping
    public ResponseEntity<List<ToyDto>> getAllToys(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String ageGroup
    ) {
        List<ToyDto> toyDTOs = toyService.getFilteredToys(category, ageGroup);
        return ResponseEntity.ok(toyDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToyDto> getToy(@PathVariable Long id) {
        ToyDto toy = toyService.getToyDTOById(id);
        return toy != null
                ? ResponseEntity.ok(toy)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToyDto> createToy(@Valid @RequestBody ToyRequestDto request) {
        ToyDto savedToy = toyService.createToy(request);
        return ResponseEntity.ok(savedToy);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteToy(@PathVariable Long id) {
        toyService.deleteToy(id); // exceptions handled globally
        return ResponseEntity.noContent().build();
    }
}
