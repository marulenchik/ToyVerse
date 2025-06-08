package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.exception.ToyDeletionException;

import java.util.List;

public interface ToyService {
    List<Toy> getAllToys(String category, String ageGroup);
    Toy getToyById(Long id);
    Toy saveToy(Toy toy);
    void deleteToy(Long id) throws ToyDeletionException;
}
