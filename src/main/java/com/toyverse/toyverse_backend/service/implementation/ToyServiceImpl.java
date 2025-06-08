package com.toyverse.toyverse_backend.service.implementation;

import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.exception.ToyDeletionException;
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

    @Override
    public void deleteToy(Long id) throws ToyDeletionException {
        Toy toy = toyRepository.findById(id)
                .orElseThrow(() -> new ToyDeletionException("Toy not found with id: " + id));
        
        if (toy.getStockQuantity() > 0) {
            throw new ToyDeletionException("Cannot delete toy with non-zero stock quantity. Current stock: " + toy.getStockQuantity());
        }

        if (toyRepository.existsByToyIdAndOrderItemsIsNotEmpty(id)) {
            throw new ToyDeletionException("Cannot delete toy that has been ordered in the past. Please archive it instead.");
        }
        
        toyRepository.deleteById(id);
    }
}
