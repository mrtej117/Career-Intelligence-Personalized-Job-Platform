package com.job.controller;

import com.job.dto.response.NotificationDTO;
import com.job.entity.JobSeeker;
import com.job.entity.User;
import com.job.service.interfaces.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(
            @RequestAttribute("user") User user) {
        JobSeeker jobSeeker = (JobSeeker) user;
        return ResponseEntity.ok(notificationService.getNotificationsFor(jobSeeker));
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @DeleteMapping
    public ResponseEntity<?> deleteAllNotifications(@RequestAttribute("user") User user) {
        JobSeeker jobSeeker = (JobSeeker) user;
        log.info("Deleting all notifications for user: {}", jobSeeker.getUsername());
        notificationService.deleteAllNotificationsForUser(jobSeeker);
        return ResponseEntity.ok("All notifications deleted successfully.");
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestAttribute("user") User user) {
        JobSeeker jobSeeker = (JobSeeker) user;
        log.info("Marking notification id: {} as read for user: {}", id, jobSeeker.getUsername());
        notificationService.markAsRead(id, jobSeeker);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@RequestAttribute("user") User user) {
        JobSeeker jobSeeker = (JobSeeker) user;
        return ResponseEntity.ok(notificationService.getUnreadCount(jobSeeker));
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @RequestAttribute("user") User user) {
        JobSeeker jobSeeker = (JobSeeker) user;
        return ResponseEntity.ok(notificationService.getUnreadNotifications(jobSeeker));
    }
}
