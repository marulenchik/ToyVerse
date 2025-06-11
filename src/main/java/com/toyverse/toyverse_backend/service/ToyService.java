package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.ToyDto;
import com.toyverse.toyverse_backend.dto.ToyRequestDto;
import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.exception.ToyDeletionException;

import java.util.List;

public interface ToyService {
    List<ToyDto> getFilteredToys(String category, String ageGroup);
    ToyDto getToyDTOById(Long id);
    ToyDto createToy(ToyRequestDto request);
    Toy getToyById(Long id);
    void deleteToy(Long id) throws ToyDeletionException;
}
