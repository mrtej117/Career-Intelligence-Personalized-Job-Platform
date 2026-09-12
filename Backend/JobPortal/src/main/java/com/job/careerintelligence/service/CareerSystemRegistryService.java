package com.job.careerintelligence.service;

import com.job.careerintelligence.dto.CareerSourceDTO;
import com.job.careerintelligence.entity.CareerSystemRegistry;
import com.job.careerintelligence.repository.CareerSystemRegistryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerSystemRegistryService {

    private final CareerSystemRegistryRepository repository;

    @Transactional
    public CareerSourceDTO createSource(CareerSourceDTO dto) {
        if (repository.existsByCompanyNameAndPlatformProviderAndBoardToken(
                dto.getCompanyName(), dto.getPlatformProvider(), dto.getBoardToken())) {
            throw new IllegalArgumentException("Source already exists for this company, platform, and board token.");
        }

        CareerSystemRegistry entity = new CareerSystemRegistry();
        entity.setCompanyName(dto.getCompanyName());
        entity.setPlatformProvider(dto.getPlatformProvider());
        entity.setBoardToken(dto.getBoardToken());
        entity.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : true);
        entity.setDateObserved(LocalDateTime.now());

        CareerSystemRegistry saved = repository.save(entity);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CareerSourceDTO> getAllSources() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CareerSourceDTO updateSource(Long id, CareerSourceDTO dto) {
        CareerSystemRegistry entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Source not found"));

        if (!entity.getCompanyName().equals(dto.getCompanyName()) ||
            !entity.getPlatformProvider().equals(dto.getPlatformProvider()) ||
            !entity.getBoardToken().equals(dto.getBoardToken())) {
            
            if (repository.existsByCompanyNameAndPlatformProviderAndBoardToken(
                    dto.getCompanyName(), dto.getPlatformProvider(), dto.getBoardToken())) {
                throw new IllegalArgumentException("Another source already exists with this combination.");
            }
        }

        entity.setCompanyName(dto.getCompanyName());
        entity.setPlatformProvider(dto.getPlatformProvider());
        entity.setBoardToken(dto.getBoardToken());
        if (dto.getIsEnabled() != null) {
            entity.setIsEnabled(dto.getIsEnabled());
        }

        CareerSystemRegistry saved = repository.save(entity);
        return mapToDTO(saved);
    }

    @Transactional
    public void setEnabled(Long id, boolean enabled) {
        CareerSystemRegistry entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Source not found"));
        entity.setIsEnabled(enabled);
        repository.save(entity);
    }

    @Transactional
    public void deleteSource(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Source not found");
        }
        repository.deleteById(id);
    }

    private CareerSourceDTO mapToDTO(CareerSystemRegistry entity) {
        CareerSourceDTO dto = new CareerSourceDTO();
        dto.setId(entity.getId());
        dto.setCompanyName(entity.getCompanyName());
        dto.setPlatformProvider(entity.getPlatformProvider());
        dto.setBoardToken(entity.getBoardToken());
        dto.setIsEnabled(entity.getIsEnabled());
        return dto;
    }
}
