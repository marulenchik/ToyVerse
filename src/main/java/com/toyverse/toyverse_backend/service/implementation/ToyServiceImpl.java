package com.toyverse.toyverse_backend.service.implementation;

import com.toyverse.toyverse_backend.dto.ToyDto;
import com.toyverse.toyverse_backend.dto.ToyRequestDto;
import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.entity.ToyStatus;
import com.toyverse.toyverse_backend.exception.ToyDeletionException;
import com.toyverse.toyverse_backend.repository.ToyRepository;
import com.toyverse.toyverse_backend.service.ToyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ToyServiceImpl implements ToyService {

    private final ToyRepository toyRepository;

    @Override
    public List<ToyDto> getFilteredToys(String category, String ageGroup) {
        List<Toy> toys;

        if (category != null && ageGroup != null) {
            toys = toyRepository.findByCategoryAndAgeGroup(category, ageGroup);
        } else if (category != null) {
            toys = toyRepository.findByCategory(category);
        } else if (ageGroup != null) {
            toys = toyRepository.findByAgeGroup(ageGroup);
        } else {
            toys = toyRepository.findAll();
        }

        return toys.stream().map(ToyDto::new).toList();
    }

    @Override
    public ToyDto getToyDTOById(Long id) {
        return toyRepository.findById(id)
                .map(ToyDto::new)
                .orElse(null);
    }

    @Override
    public ToyDto createToy(ToyRequestDto request) {
        Toy toy = new Toy();
        toy.setName(request.getName());
        toy.setCategory(request.getCategory());
        toy.setAgeGroup(request.getAgeGroup());
        toy.setPrice(request.getPrice());
        toy.setDescription(request.getDescription());
        toy.setImageUrl(request.getImageUrl());
        toy.setStockQuantity(request.getStockQuantity());

        return new ToyDto(toyRepository.save(toy));
    }

    @Override
    public void deleteToy(Long id) {
        Toy toy = toyRepository.findById(id)
                .orElseThrow(() -> new ToyDeletionException("Toy not found with id: " + id));

        toy.setStatus(ToyStatus.DISCONTINUED);
        toyRepository.save(toy);
    }

    @Override
    public Toy getToyById(Long id){
        return toyRepository.findById(id).orElse(null);
    }
}
