package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.RawJobObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RawJobObservationRepository extends JpaRepository<RawJobObservation, Long> {
    Optional<RawJobObservation> findFirstByJobIdentityIdOrderByCollectionTimestampDesc(Long identityId);
    java.util.List<RawJobObservation> findAllByOrderByCollectionTimestampDesc();
    long countByJobIdentityId(Long identityId);
}
