package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.UniversalJobRepresentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversalJobRepresentationRepository extends JpaRepository<UniversalJobRepresentation, Long> {
    UniversalJobRepresentation findByRawObservationId(Long rawObservationId);
}
