package com.toyverse.toyverse_backend.service.implementation;

import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.repository.ToyRepository;
import com.toyverse.toyverse_backend.service.ToyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToyServiceImpl implements ToyService {

    private final ToyRepository toyRepository;

    public ToyServiceImpl(ToyRepository toyRepository) {
        this.toyRepository = toyRepository;
    }

    @Override
    public List<Toy> getAllToys(String category, String ageGroup) {
        if (category != null && ageGroup != null) {
            return toyRepository.findByCategoryAndAgeGroup(category, ageGroup);
        } else if (category != null) {
            return toyRepository.findByCategory(category);
        } else if (ageGroup != null) {
            return toyRepository.findByAgeGroup(ageGroup);
        } else {
            return toyRepository.findAll();
        }
    }

    @Override
    public Toy getToyById(Long id) {
        return toyRepository.findById(id).orElse(null);
    }

    @Override
    public Toy saveToy(Toy toy) {
        return toyRepository.save(toy);
    }
}
