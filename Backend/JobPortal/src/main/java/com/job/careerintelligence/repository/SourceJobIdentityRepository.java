package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.SourceJobIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SourceJobIdentityRepository extends JpaRepository<SourceJobIdentity, Long> {
    Optional<SourceJobIdentity> findBySourcePlatformAndCompanyNameAndExternalJobId(String sourcePlatform, String companyName, String externalJobId);
    
    java.util.List<SourceJobIdentity> findBySourcePlatformAndCompanyNameAndIsActiveTrue(String sourcePlatform, String companyName);
    java.util.List<SourceJobIdentity> findByIsActiveTrue();
}
