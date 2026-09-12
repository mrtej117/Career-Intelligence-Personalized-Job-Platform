package com.job.designpatterns.Observer;

import com.job.entity.Application;
import com.job.entity.JobSeeker;

public interface ApplicationObserver {
    void notify(JobSeeker jobSeeker, Application application);
}
