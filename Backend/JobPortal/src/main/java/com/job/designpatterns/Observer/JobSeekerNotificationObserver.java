package com.job.designpatterns.Observer;

import com.job.entity.Application;
import com.job.entity.JobSeeker;
import com.job.entity.Notification;
import com.job.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobSeekerNotificationObserver implements ApplicationObserver {

    private final NotificationRepository notificationRepository;

    @Override
    public void notify(JobSeeker jobSeeker, Application application) {
        String message = String.format(
                "Update: Your application for '%s' at %s has been %s.",
                application.getJob().getTitle(),
                application.getJob().getEmployer().getCompanyName(),
                application.getStatus().name().substring(0, 1).toUpperCase() + application.getStatus().name().substring(1).toLowerCase()
        );

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setRecipient(jobSeeker);
        notification.setApplication(application);
        notificationRepository.save(notification);
        log.info("Notification created for user: {} regarding application for job: '{}'",
                jobSeeker.getUsername(), application.getJob().getTitle());
    }
}
