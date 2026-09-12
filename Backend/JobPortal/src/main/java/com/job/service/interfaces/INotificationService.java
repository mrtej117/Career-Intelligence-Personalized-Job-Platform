package com.job.service.interfaces;

import com.job.dto.response.NotificationDTO;
import com.job.entity.JobSeeker;
import com.job.entity.Notification;

import java.util.List;

public interface INotificationService {
    List<NotificationDTO> getNotificationsFor(JobSeeker jobSeeker);
    NotificationDTO mapToDTO(Notification notification);
    void deleteAllNotificationsForUser(JobSeeker jobSeeker);
    void markAsRead(Long notificationId, JobSeeker jobSeeker);
    int getUnreadCount(JobSeeker jobSeeker);
    List<NotificationDTO> getUnreadNotifications(JobSeeker jobSeeker);
}
