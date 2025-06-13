package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.ToyDto;
import com.toyverse.toyverse_backend.dto.ToyRequestDto;
import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.entity.ToyStatus;
import com.toyverse.toyverse_backend.exception.ToyDeletionException;
import com.toyverse.toyverse_backend.repository.ToyRepository;
import com.toyverse.toyverse_backend.service.implementation.ToyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToyServiceImplTest {

    @Mock
    private ToyRepository toyRepository;

    @InjectMocks
    private ToyServiceImpl toyService;

    private Toy toy1;
    private Toy toy2;
    private Toy toy3;
    private ToyRequestDto toyRequest;

    @BeforeEach
    void setUp() {
        toy1 = new Toy();
        toy1.setToyId(1L);
        toy1.setName("Action Figure");
        toy1.setCategory("Action");
        toy1.setAgeGroup("6-12");
        toy1.setPrice(29.99);
        toy1.setDescription("Great action figure");
        toy1.setImageUrl("http://example.com/toy1.jpg");
        toy1.setStockQuantity(10);
        toy1.setStatus(ToyStatus.ACTIVE);

        toy2 = new Toy();
        toy2.setToyId(2L);
        toy2.setName("Educational Puzzle");
        toy2.setCategory("Educational");
        toy2.setAgeGroup("3-6");
        toy2.setPrice(19.99);
        toy2.setDescription("Learning puzzle");
        toy2.setImageUrl("http://example.com/toy2.jpg");
        toy2.setStockQuantity(5);
        toy2.setStatus(ToyStatus.ACTIVE);

        toy3 = new Toy();
        toy3.setToyId(3L);
        toy3.setName("Building Blocks");
        toy3.setCategory("Educational");
        toy3.setAgeGroup("6-12");
        toy3.setPrice(39.99);
        toy3.setDescription("Creative building blocks");
        toy3.setImageUrl("http://example.com/toy3.jpg");
        toy3.setStockQuantity(8);
        toy3.setStatus(ToyStatus.ACTIVE);

        toyRequest = new ToyRequestDto();
        toyRequest.setName("New Toy");
        toyRequest.setCategory("Action");
        toyRequest.setAgeGroup("6-12");
        toyRequest.setPrice(24.99);
        toyRequest.setDescription("Brand new toy");
        toyRequest.setImageUrl("http://example.com/newtoy.jpg");
        toyRequest.setStockQuantity(15);
    }

    @Test
    void getFilteredToys_ShouldReturnAllToys_WhenNoFiltersApplied() {
        // Given
        List<Toy> allToys = Arrays.asList(toy1, toy2, toy3);
        when(toyRepository.findAll()).thenReturn(allToys);

        // When
        List<ToyDto> result = toyService.getFilteredToys(null, null);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Action Figure", result.get(0).getName());
        assertEquals("Educational Puzzle", result.get(1).getName());
        assertEquals("Building Blocks", result.get(2).getName());

        verify(toyRepository).findAll();
        verify(toyRepository, never()).findByCategory(anyString());
        verify(toyRepository, never()).findByAgeGroup(anyString());
        verify(toyRepository, never()).findByCategoryAndAgeGroup(anyString(), anyString());
    }

    @Test
    void getFilteredToys_ShouldReturnToysByCategory_WhenCategoryFilterApplied() {
        // Given
        String category = "Educational";
        List<Toy> educationalToys = Arrays.asList(toy2, toy3);
        when(toyRepository.findByCategory(category)).thenReturn(educationalToys);

        // When
        List<ToyDto> result = toyService.getFilteredToys(category, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Educational Puzzle", result.get(0).getName());
        assertEquals("Building Blocks", result.get(1).getName());
        assertTrue(result.stream().allMatch(toy -> toy.getCategory().equals(category)));

        verify(toyRepository).findByCategory(category);
        verify(toyRepository, never()).findAll();
        verify(toyRepository, never()).findByAgeGroup(anyString());
        verify(toyRepository, never()).findByCategoryAndAgeGroup(anyString(), anyString());
    }

    @Test
    void getFilteredToys_ShouldReturnToysByAgeGroup_WhenAgeGroupFilterApplied() {
        // Given
        String ageGroup = "6-12";
        List<Toy> ageGroupToys = Arrays.asList(toy1, toy3);
        when(toyRepository.findByAgeGroup(ageGroup)).thenReturn(ageGroupToys);

        // When
        List<ToyDto> result = toyService.getFilteredToys(null, ageGroup);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Action Figure", result.get(0).getName());
        assertEquals("Building Blocks", result.get(1).getName());
        assertTrue(result.stream().allMatch(toy -> toy.getAgeGroup().equals(ageGroup)));

        verify(toyRepository).findByAgeGroup(ageGroup);
        verify(toyRepository, never()).findAll();
        verify(toyRepository, never()).findByCategory(anyString());
        verify(toyRepository, never()).findByCategoryAndAgeGroup(anyString(), anyString());
    }

    @Test
    void getFilteredToys_ShouldReturnToysByBothFilters_WhenBothFiltersApplied() {
        // Given
        String category = "Educational";
        String ageGroup = "6-12";
        List<Toy> filteredToys = Arrays.asList(toy3);
        when(toyRepository.findByCategoryAndAgeGroup(category, ageGroup)).thenReturn(filteredToys);

        // When
        List<ToyDto> result = toyService.getFilteredToys(category, ageGroup);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Building Blocks", result.get(0).getName());
        assertEquals(category, result.get(0).getCategory());
        assertEquals(ageGroup, result.get(0).getAgeGroup());

        verify(toyRepository).findByCategoryAndAgeGroup(category, ageGroup);
        verify(toyRepository, never()).findAll();
        verify(toyRepository, never()).findByCategory(anyString());
        verify(toyRepository, never()).findByAgeGroup(anyString());
    }

    @Test
    void getToyDTOById_ShouldReturnToyDto_WhenToyExists() {
        // Given
        Long toyId = 1L;
        when(toyRepository.findById(toyId)).thenReturn(Optional.of(toy1));

        // When
        ToyDto result = toyService.getToyDTOById(toyId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getToyId());
        assertEquals("Action Figure", result.getName());
        assertEquals("Action", result.getCategory());
        assertEquals("6-12", result.getAgeGroup());
        assertEquals(29.99, result.getPrice());

        verify(toyRepository).findById(toyId);
    }

    @Test
    void getToyDTOById_ShouldReturnNull_WhenToyDoesNotExist() {
        // Given
        Long toyId = 999L;
        when(toyRepository.findById(toyId)).thenReturn(Optional.empty());

        // When
        ToyDto result = toyService.getToyDTOById(toyId);

        // Then
        assertNull(result);
        verify(toyRepository).findById(toyId);
    }

    @Test
    void createToy_ShouldReturnToyDto_WhenValidRequest() {
        // Given
        Toy savedToy = new Toy();
        savedToy.setToyId(4L);
        savedToy.setName("New Toy");
        savedToy.setCategory("Action");
        savedToy.setAgeGroup("6-12");
        savedToy.setPrice(24.99);
        savedToy.setDescription("Brand new toy");
        savedToy.setImageUrl("http://example.com/newtoy.jpg");
        savedToy.setStockQuantity(15);

        when(toyRepository.save(any(Toy.class))).thenReturn(savedToy);

        // When
        ToyDto result = toyService.createToy(toyRequest);

        // Then
        assertNotNull(result);
        assertEquals(4L, result.getToyId());
        assertEquals("New Toy", result.getName());
        assertEquals("Action", result.getCategory());
        assertEquals("6-12", result.getAgeGroup());
        assertEquals(24.99, result.getPrice());
        assertEquals("Brand new toy", result.getDescription());
        assertEquals("http://example.com/newtoy.jpg", result.getImageUrl());
        assertEquals(15, result.getStockQuantity());

        verify(toyRepository).save(any(Toy.class));
    }

    @Test
    void createToy_ShouldSaveToyWithCorrectData() {
        // Given
        when(toyRepository.save(any(Toy.class))).thenReturn(toy1);

        // When
        toyService.createToy(toyRequest);

        // Then
        verify(toyRepository).save(argThat(toy -> 
            toy.getName().equals("New Toy") &&
            toy.getCategory().equals("Action") &&
            toy.getAgeGroup().equals("6-12") &&
            toy.getPrice().equals(24.99) &&
            toy.getDescription().equals("Brand new toy") &&
            toy.getImageUrl().equals("http://example.com/newtoy.jpg") &&
            toy.getStockQuantity().equals(15)
        ));
    }

    @Test
    void deleteToy_ShouldSetStatusToDiscontinued_WhenToyExists() {
        // Given
        Long toyId = 1L;
        when(toyRepository.findById(toyId)).thenReturn(Optional.of(toy1));
        when(toyRepository.save(any(Toy.class))).thenReturn(toy1);

        // When
        toyService.deleteToy(toyId);

        // Then
        verify(toyRepository).findById(toyId);
        verify(toyRepository).save(argThat(toy -> 
            toy.getToyId().equals(toyId) &&
            toy.getStatus().equals(ToyStatus.DISCONTINUED)
        ));
    }

    @Test
    void deleteToy_ShouldThrowException_WhenToyDoesNotExist() {
        // Given
        Long toyId = 999L;
        when(toyRepository.findById(toyId)).thenReturn(Optional.empty());

        // When & Then
        ToyDeletionException exception = assertThrows(
                ToyDeletionException.class,
                () -> toyService.deleteToy(toyId)
        );

        assertEquals("Toy not found with id: " + toyId, exception.getMessage());
        verify(toyRepository).findById(toyId);
        verify(toyRepository, never()).save(any(Toy.class));
    }

    @Test
    void getToyById_ShouldReturnToy_WhenToyExists() {
        // Given
        Long toyId = 1L;
        when(toyRepository.findById(toyId)).thenReturn(Optional.of(toy1));

        // When
        Toy result = toyService.getToyById(toyId);

        // Then
        assertNotNull(result);
        assertEquals(toy1, result);
        verify(toyRepository).findById(toyId);
    }

    @Test
    void getToyById_ShouldReturnNull_WhenToyDoesNotExist() {
        // Given
        Long toyId = 999L;
        when(toyRepository.findById(toyId)).thenReturn(Optional.empty());

        // When
        Toy result = toyService.getToyById(toyId);

        // Then
        assertNull(result);
        verify(toyRepository).findById(toyId);
    }

    @Test
    void getFilteredToys_ShouldReturnEmptyList_WhenNoToysMatch() {
        // Given
        String category = "NonExistentCategory";
        when(toyRepository.findByCategory(category)).thenReturn(Arrays.asList());

        // When
        List<ToyDto> result = toyService.getFilteredToys(category, null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(toyRepository).findByCategory(category);
    }

    @Test
    void createToy_ShouldHandleNullValues() {
        // Given
        ToyRequestDto requestWithNulls = new ToyRequestDto();
        requestWithNulls.setName(null);
        requestWithNulls.setCategory(null);
        requestWithNulls.setAgeGroup(null);
        requestWithNulls.setPrice(null);
        requestWithNulls.setDescription(null);
        requestWithNulls.setImageUrl(null);
        requestWithNulls.setStockQuantity(null);

        Toy toyWithNulls = new Toy();
        toyWithNulls.setToyId(5L);
        when(toyRepository.save(any(Toy.class))).thenReturn(toyWithNulls);

        // When
        ToyDto result = toyService.createToy(requestWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(5L, result.getToyId());
        verify(toyRepository).save(any(Toy.class));
    }

    @Test
    void getFilteredToys_ShouldConvertToysToToyDtos() {
        // Given
        List<Toy> toys = Arrays.asList(toy1);
        when(toyRepository.findAll()).thenReturn(toys);

        // When
        List<ToyDto> result = toyService.getFilteredToys(null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        ToyDto toyDto = result.get(0);
        assertEquals(toy1.getToyId(), toyDto.getToyId());
        assertEquals(toy1.getName(), toyDto.getName());
        assertEquals(toy1.getCategory(), toyDto.getCategory());
        assertEquals(toy1.getAgeGroup(), toyDto.getAgeGroup());
        assertEquals(toy1.getPrice(), toyDto.getPrice());
        assertEquals(toy1.getDescription(), toyDto.getDescription());
        assertEquals(toy1.getImageUrl(), toyDto.getImageUrl());
        assertEquals(toy1.getStockQuantity(), toyDto.getStockQuantity());
    }
} 