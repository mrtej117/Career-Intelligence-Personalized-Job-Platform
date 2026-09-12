package com.job.service.impl;

import com.job.dto.response.NotificationDTO;
import com.job.entity.Application;
import com.job.entity.JobSeeker;
import com.job.entity.Notification;
import com.job.exception.ResourceNotFoundException;
import com.job.exception.UnauthorizedException;
import com.job.repository.NotificationRepository;
import com.job.service.interfaces.INotificationService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsFor(JobSeeker jobSeeker) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(jobSeeker).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setSeen(notification.isSeen());

        if (notification.getApplication() != null) {
            dto.setApplicationId(notification.getApplication().getId());

            Application app = notification.getApplication();
            if (app.getJob() != null && app.getJob().getEmployer() != null) {
                String logoUrl = app.getJob().getEmployer().getProfilePictureUrl();
                if (logoUrl != null) {
                    dto.setCompanyLogoUrl(logoUrl);
                }
            }
        }

        return dto;
    }

    @Override
    @Transactional
    public void deleteAllNotificationsForUser(JobSeeker jobSeeker) {
        notificationRepository.deleteAllByRecipient(jobSeeker);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, JobSeeker jobSeeker) {
        log.info("Marking notification id: {} as read for user: {}", notificationId, jobSeeker.getUsername());
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(jobSeeker.getId())) {
            throw new UnauthorizedException("You are not authorized to mark this notification as read");
        }

        notification.setSeen(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(JobSeeker jobSeeker) {
        return notificationRepository.countByRecipientAndSeenFalse(jobSeeker);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(JobSeeker jobSeeker) {
        return notificationRepository.findByRecipientAndSeenFalseOrderByCreatedAtDesc(jobSeeker).stream()
                .map(this::mapToDTO)
                .toList();
    }
}
