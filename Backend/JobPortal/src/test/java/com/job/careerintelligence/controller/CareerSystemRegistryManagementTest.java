package com.job.careerintelligence.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.careerintelligence.dto.CareerSourceDTO;
import com.job.careerintelligence.entity.CareerSystemRegistry;
import com.job.careerintelligence.repository.CareerSystemRegistryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
public class CareerSystemRegistryManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CareerSystemRegistryRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateGreenhouseSource() throws Exception {
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setCompanyName("GitLab");
        dto.setPlatformProvider("Greenhouse");
        dto.setBoardToken("gitlab");
        dto.setIsEnabled(true);

        mockMvc.perform(post("/api/career-intelligence/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.companyName").value("GitLab"))
                .andExpect(jsonPath("$.platformProvider").value("Greenhouse"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateLeverSource() throws Exception {
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setCompanyName("Shopify");
        dto.setPlatformProvider("Lever");
        dto.setBoardToken("shopify");
        dto.setIsEnabled(false);

        mockMvc.perform(post("/api/career-intelligence/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.isEnabled").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDuplicateRejection() throws Exception {
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setCompanyName("Duplicate");
        dto.setPlatformProvider("Greenhouse");
        dto.setBoardToken("dup");

        // First creation succeeds
        mockMvc.perform(post("/api/career-intelligence/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Second creation fails
        mockMvc.perform(post("/api/career-intelligence/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateAshbySource() throws Exception {
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setCompanyName("AshbyCo");
        dto.setPlatformProvider("Ashby");
        dto.setBoardToken("ashbyco");
        dto.setIsEnabled(true);

        mockMvc.perform(post("/api/career-intelligence/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.platformProvider").value("Ashby"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateWorkdaySource() throws Exception {
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setCompanyName("WorkdayCo");
        dto.setPlatformProvider("Workday");
        dto.setBoardToken("tenant/site");
        dto.setIsEnabled(true);

        mockMvc.perform(post("/api/career-intelligence/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.platformProvider").value("Workday"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInvalidPlatformRejection() throws Exception {
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setCompanyName("Invalid");
        dto.setPlatformProvider("Breezy"); // Not supported yet
        dto.setBoardToken("invalid");

        mockMvc.perform(post("/api/career-intelligence/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateAndEnableDisable() throws Exception {
        CareerSystemRegistry entity = new CareerSystemRegistry();
        entity.setCompanyName("UpdateMe");
        entity.setPlatformProvider("Greenhouse");
        entity.setBoardToken("updateme");
        entity.setIsEnabled(true);
        entity = repository.save(entity);

        // Update name
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setCompanyName("UpdatedName");
        dto.setPlatformProvider("Greenhouse");
        dto.setBoardToken("updateme");
        dto.setIsEnabled(true);

        mockMvc.perform(put("/api/career-intelligence/sources/" + entity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("UpdatedName"));

        // Disable via PATCH
        mockMvc.perform(patch("/api/career-intelligence/sources/" + entity.getId() + "/enabled")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("enabled", false))))
                .andExpect(status().isNoContent());

        CareerSystemRegistry updated = repository.findById(entity.getId()).get();
        assertFalse(updated.getIsEnabled());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDelete() throws Exception {
        CareerSystemRegistry entity = new CareerSystemRegistry();
        entity.setCompanyName("DeleteMe");
        entity.setPlatformProvider("Greenhouse");
        entity.setBoardToken("deleteme");
        entity = repository.save(entity);

        mockMvc.perform(delete("/api/career-intelligence/sources/" + entity.getId()))
                .andExpect(status().isNoContent());

        assertFalse(repository.existsById(entity.getId()));
    }

    @Test
    @WithMockUser(roles = "JOB_SEEKER")
    public void testAdminAuthorization() throws Exception {
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setCompanyName("Test");
        dto.setPlatformProvider("Greenhouse");
        dto.setBoardToken("test");

        mockMvc.perform(post("/api/career-intelligence/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
