package com.job.repository;

import com.job.entity.JobSeeker;
import com.job.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n JOIN FETCH n.application a JOIN FETCH a.job j JOIN FETCH j.employer WHERE n.recipient = :recipient ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientOrderByCreatedAtDesc(@Param("recipient") JobSeeker recipient);

    void deleteAllByRecipient(JobSeeker recipient);
    int countByRecipientAndSeenFalse(JobSeeker jobSeeker);

    @Query("SELECT n FROM Notification n JOIN FETCH n.application a JOIN FETCH a.job j JOIN FETCH j.employer WHERE n.recipient = :recipient AND n.seen = false ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientAndSeenFalseOrderByCreatedAtDesc(@Param("recipient") JobSeeker recipient);
}
