package com.topcinnamon.repository;

import com.topcinnamon.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Find by edit token
    Optional<Contact> findByEditToken(String editToken);

    // Find unread contacts older than specific date
    List<Contact> findByReadByAdminFalseAndCreatedAtBefore(LocalDateTime date);

    // Find editable contacts older than specific date
    List<Contact> findByCanEditTrueAndCreatedAtBefore(LocalDateTime date);

    // Find active submissions (not archived)
    List<Contact> findByArchivedFalseOrderByCreatedAtDesc();

    // Find archived submissions
    List<Contact> findByArchivedTrueOrderByReviewedAtDesc();

    // Find reviewed but not archived contacts older than specific date
    List<Contact> findByReadByAdminTrueAndArchivedFalseAndCreatedAtBefore(LocalDateTime date);

    // Get all contacts ordered by creation date
    List<Contact> findAllByOrderByCreatedAtDesc();

    List<Contact> findByEmailAndPhoneOrderByCreatedAtDesc(String email, String phone);

    List<Contact> findByEmailAndPhoneAndCreatedAtAfterOrderByCreatedAtDesc(
            String email, String phone, LocalDateTime date);
}