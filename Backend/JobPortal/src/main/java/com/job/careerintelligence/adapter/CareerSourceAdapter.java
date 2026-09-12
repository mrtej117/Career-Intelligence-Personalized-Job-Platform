package com.job.careerintelligence.adapter;

import com.job.careerintelligence.dto.RawJobDTO;
import java.util.List;

public interface CareerSourceAdapter {
    String getPlatformName();
    
    // Check if this adapter can handle a given company identifier or url
    boolean canHandle(String identifier);
    
    // Discover jobs from a board/company identifier
    List<RawJobDTO> discoverJobs(String identifier);
    
    // Fetch detailed info for a single job
    RawJobDTO fetchJobDetails(String identifier, String externalJobId);
}
