package com.livingrank.controller;

import com.livingrank.dto.CriteriaAverages;
import com.livingrank.dto.StreetDetailResponse;
import com.livingrank.dto.StreetResponse;
import com.livingrank.exception.ResourceNotFoundException;
import com.livingrank.security.JwtAuthenticationFilter;
import com.livingrank.security.JwtTokenProvider;
import com.livingrank.service.StreetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StreetController.class)
class StreetControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private StreetService streetService;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void searchStreets_shouldReturnResults() throws Exception {
        StreetResponse street = new StreetResponse(1L, "Berliner Str.", "10115", "Berlin", "Berlin", "DE", 52.52, 13.405, 4.2, 5L);
        when(streetService.searchStreets("Berlin")).thenReturn(List.of(street));

        mockMvc.perform(get("/api/streets/search").param("q", "Berlin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].streetName").value("Berliner Str."))
            .andExpect(jsonPath("$[0].averageRating").value(4.2));
    }

    @Test
    void searchStreets_emptyResults_shouldReturn200() throws Exception {
        when(streetService.searchStreets("Nirgendwo")).thenReturn(List.of());

        mockMvc.perform(get("/api/streets/search").param("q", "Nirgendwo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getStreetDetail_shouldReturnDetail() throws Exception {
        StreetResponse street = new StreetResponse(1L, "Teststr.", "80331", "München", "Bayern", "DE", 48.13, 11.58, 3.8, 10L);
        CriteriaAverages criteria = new CriteriaAverages(3.0, 4.0, 3.5, 4.0, 3.5, 2.5, 2.0, 3.0, 3.0, 4.0, 3.5, 3.0, 4.0, 3.5, 1.5, 4.0, 3.5, 3.0);
        StreetDetailResponse detail = new StreetDetailResponse(street, criteria, false);

        when(streetService.getStreetDetail(eq(1L), any())).thenReturn(detail);

        mockMvc.perform(get("/api/streets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.street.streetName").value("Teststr."))
            .andExpect(jsonPath("$.userHasReviewed").value(false));
    }

    @Test
    void getStreetDetail_nonExistent_shouldReturn404() throws Exception {
        when(streetService.getStreetDetail(eq(999L), any())).thenThrow(
            new ResourceNotFoundException("Straße nicht gefunden.")
        );

        mockMvc.perform(get("/api/streets/999"))
            .andExpect(status().isNotFound());
    }
}
