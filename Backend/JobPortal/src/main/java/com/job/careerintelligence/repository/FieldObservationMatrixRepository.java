package com.job.careerintelligence.repository;

import com.job.careerintelligence.entity.FieldObservationMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldObservationMatrixRepository extends JpaRepository<FieldObservationMatrix, Long> {}
