package com.toyverse.toyverse_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyverse.toyverse_backend.dto.ToyDto;
import com.toyverse.toyverse_backend.dto.ToyRequestDto;
import com.toyverse.toyverse_backend.service.ToyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToyController.class)
class ToyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ToyService toyService;

    @Autowired
    private ObjectMapper objectMapper;

    private ToyDto toyDto;
    private ToyRequestDto toyRequest;

    @BeforeEach
    void setUp() {
        // Create a mock Toy entity first
        com.toyverse.toyverse_backend.entity.Toy mockToy = new com.toyverse.toyverse_backend.entity.Toy();
        mockToy.setToyId(1L);
        mockToy.setName("Test Toy");
        mockToy.setCategory("Action Figure");
        mockToy.setAgeGroup("6-12");
        mockToy.setPrice(29.99);
        mockToy.setDescription("A great test toy");
        mockToy.setImageUrl("http://example.com/toy.jpg");
        mockToy.setStockQuantity(10);
        
        toyDto = new ToyDto(mockToy);

        toyRequest = new ToyRequestDto();
        toyRequest.setName("New Toy");
        toyRequest.setCategory("Educational");
        toyRequest.setAgeGroup("3-6");
        toyRequest.setPrice(19.99);
        toyRequest.setDescription("Educational toy for kids");
        toyRequest.setImageUrl("http://example.com/newtoy.jpg");
        toyRequest.setStockQuantity(20);
    }

    @Test
    void getAllToys_ShouldReturnToysList_WhenNoFiltersApplied() throws Exception {
        // Given
        List<ToyDto> toys = Arrays.asList(toyDto);
        when(toyService.getFilteredToys(null, null)).thenReturn(toys);

        // When & Then
        mockMvc.perform(get("/api/toys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].toyId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Toy"))
                .andExpect(jsonPath("$[0].category").value("Action Figure"))
                .andExpect(jsonPath("$[0].ageGroup").value("6-12"))
                .andExpect(jsonPath("$[0].price").value(29.99));

        verify(toyService).getFilteredToys(null, null);
    }

    @Test
    void getAllToys_ShouldReturnFilteredToys_WhenCategoryFilterApplied() throws Exception {
        // Given
        String category = "Action Figure";
        List<ToyDto> toys = Arrays.asList(toyDto);
        when(toyService.getFilteredToys(category, null)).thenReturn(toys);

        // When & Then
        mockMvc.perform(get("/api/toys")
                        .param("category", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value(category));

        verify(toyService).getFilteredToys(category, null);
    }

    @Test
    void getAllToys_ShouldReturnFilteredToys_WhenAgeGroupFilterApplied() throws Exception {
        // Given
        String ageGroup = "6-12";
        List<ToyDto> toys = Arrays.asList(toyDto);
        when(toyService.getFilteredToys(null, ageGroup)).thenReturn(toys);

        // When & Then
        mockMvc.perform(get("/api/toys")
                        .param("ageGroup", ageGroup))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ageGroup").value(ageGroup));

        verify(toyService).getFilteredToys(null, ageGroup);
    }

    @Test
    void getAllToys_ShouldReturnFilteredToys_WhenBothFiltersApplied() throws Exception {
        // Given
        String category = "Action Figure";
        String ageGroup = "6-12";
        List<ToyDto> toys = Arrays.asList(toyDto);
        when(toyService.getFilteredToys(category, ageGroup)).thenReturn(toys);

        // When & Then
        mockMvc.perform(get("/api/toys")
                        .param("category", category)
                        .param("ageGroup", ageGroup))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(toyService).getFilteredToys(category, ageGroup);
    }

    @Test
    void getToy_ShouldReturnToy_WhenToyExists() throws Exception {
        // Given
        Long toyId = 1L;
        when(toyService.getToyDTOById(toyId)).thenReturn(toyDto);

        // When & Then
        mockMvc.perform(get("/api/toys/{id}", toyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toyId").value(1L))
                .andExpect(jsonPath("$.name").value("Test Toy"))
                .andExpect(jsonPath("$.price").value(29.99));

        verify(toyService).getToyDTOById(toyId);
    }

    @Test
    void getToy_ShouldReturnNotFound_WhenToyDoesNotExist() throws Exception {
        // Given
        Long toyId = 999L;
        when(toyService.getToyDTOById(toyId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/toys/{id}", toyId))
                .andExpect(status().isNotFound());

        verify(toyService).getToyDTOById(toyId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createToy_ShouldReturnCreatedToy_WhenValidRequest() throws Exception {
        // Given
        when(toyService.createToy(any(ToyRequestDto.class))).thenReturn(toyDto);

        // When & Then
        mockMvc.perform(post("/api/toys")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toyId").value(1L))
                .andExpect(jsonPath("$.name").value("Test Toy"));

        verify(toyService).createToy(any(ToyRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createToy_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/toys")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toyRequest)))
                .andExpect(status().isForbidden());

        verify(toyService, never()).createToy(any());
    }

    @Test
    void createToy_ShouldReturnUnauthorized_WhenUserNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/toys")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toyRequest)))
                .andExpect(status().isUnauthorized());

        verify(toyService, never()).createToy(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createToy_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Given
        ToyRequestDto invalidRequest = new ToyRequestDto();
        invalidRequest.setName(""); // Empty name
        invalidRequest.setPrice(-10.0); // Negative price

        // When & Then
        mockMvc.perform(post("/api/toys")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(toyService, never()).createToy(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteToy_ShouldReturnNoContent_WhenToyExists() throws Exception {
        // Given
        Long toyId = 1L;
        doNothing().when(toyService).deleteToy(toyId);

        // When & Then
        mockMvc.perform(delete("/api/toys/{id}", toyId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(toyService).deleteToy(toyId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteToy_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        // Given
        Long toyId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/toys/{id}", toyId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(toyService, never()).deleteToy(any());
    }

    @Test
    void deleteToy_ShouldReturnUnauthorized_WhenUserNotAuthenticated() throws Exception {
        // Given
        Long toyId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/toys/{id}", toyId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(toyService, never()).deleteToy(any());
    }
} 