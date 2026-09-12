package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.CareerSystemRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerSystemRegistryRepository extends JpaRepository<CareerSystemRegistry, Long> {
    boolean existsByCompanyNameAndPlatformProviderAndBoardToken(String companyName, String platformProvider, String boardToken);
}
