package com.toyverse.toyverse_backend.controller;

import com.toyverse.toyverse_backend.dto.ToyDTO;
import com.toyverse.toyverse_backend.dto.ToyRequest;
import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.exception.ToyDeletionException;
import com.toyverse.toyverse_backend.service.ToyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/toys")
public class ToyController {
    private final ToyService toyService;

    public ToyController(ToyService toyService) {
        this.toyService = toyService;
    }

    @GetMapping
    public ResponseEntity<List<ToyDTO>> getAllToys(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String ageGroup
    ) {
        List<Toy> toys = toyService.getAllToys(category, ageGroup);
        List<ToyDTO> toyDTOs = toys.stream()
                .map(ToyDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(toyDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToyDTO> getToy(@PathVariable Long id) {
        Toy toy = toyService.getToyById(id);
        if (toy == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ToyDTO(toy));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ToyDTO> createToy(@Valid @RequestBody ToyRequest request) {
        Toy toy = new Toy();
        toy.setName(request.getName());
        toy.setCategory(request.getCategory());
        toy.setAgeGroup(request.getAgeGroup());
        toy.setPrice(request.getPrice());
        toy.setDescription(request.getDescription());
        toy.setImageUrl(request.getImageUrl());
        toy.setStockQuantity(request.getStockQuantity());

        Toy savedToy = toyService.saveToy(toy);
        return ResponseEntity.ok(new ToyDTO(savedToy));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteToy(@PathVariable Long id) {
        try {
            toyService.deleteToy(id);
            return ResponseEntity.noContent().build();
        } catch (ToyDeletionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}