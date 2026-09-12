package com.job.careerintelligence.dto;

import com.job.careerintelligence.entity.RawJobObservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveObservationResult {
    private RawJobObservation observation;
    private ObservationStatus status;
    
    public enum ObservationStatus {
        NEW,
        CHANGED,
        UNCHANGED
    }
}
