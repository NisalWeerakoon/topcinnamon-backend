package com.topcinnamon.service;

import com.topcinnamon.models.Contact;
import com.topcinnamon.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map; // Add this import
import java.util.HashMap; // Add this import
import java.util.stream.Collectors; // Add this import

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact saveContact(Contact contact) {
        // This will save regardless of duplicates - NO VALIDATION FOR DUPLICATES
        System.out.println("Saving contact - Name: " + contact.getName() +
                ", Email: " + contact.getEmail() +
                ", Phone: " + contact.getPhone());
        return contactRepository.save(contact);
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public List<Contact> getAllContactsOrderedByDate() {
        return contactRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    public Optional<Contact> getContactByEditToken(String editToken) {
        return contactRepository.findByEditToken(editToken);
    }

    public void markAsRead(Long id) {
        contactRepository.findById(id).ifPresent(contact -> {
            contact.setReadByAdmin(true);
            contactRepository.save(contact);
        });
    }

    public List<Contact> getActiveSubmissions() {
        return contactRepository.findByArchivedFalseOrderByCreatedAtDesc();
    }

    public List<Contact> getArchivedSubmissions() {
        return contactRepository.findByArchivedTrueOrderByReviewedAtDesc();
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    public List<Contact> findPreviousSubmissions(String email, String phone) {
        List<Contact> submissions = contactRepository.findByEmailAndPhoneOrderByCreatedAtDesc(email, phone);
        System.out.println("Found " + submissions.size() + " previous submissions for email: " + email);
        return submissions;
    }

    public List<Contact> findRecentSubmissions(String email, String phone) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return contactRepository.findByEmailAndPhoneAndCreatedAtAfterOrderByCreatedAtDesc(
                email, phone, thirtyDaysAgo);
    }

    // Enhanced methods for dashboard functionality
    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            List<Contact> allContacts = contactRepository.findAll();

            stats.put("totalMessages", allContacts.size());
            stats.put("pendingMessages", allContacts.stream().filter(c -> "Pending".equals(c.getStatus())).count());
            stats.put("approvedMessages", allContacts.stream().filter(c -> "Approved".equals(c.getStatus())).count());
            stats.put("rejectedMessages", allContacts.stream().filter(c -> "Rejected".equals(c.getStatus())).count());

            // Recent submissions (last 24 hours)
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            stats.put("recentSubmissions", allContacts.stream()
                    .filter(c -> c.getCreatedAt() != null && c.getCreatedAt().isAfter(yesterday))
                    .count());

        } catch (Exception e) {
            // Set default values
            stats.put("totalMessages", 0);
            stats.put("pendingMessages", 0);
            stats.put("approvedMessages", 0);
            stats.put("rejectedMessages", 0);
            stats.put("recentSubmissions", 0);
        }

        return stats;
    }

    // Export functionality
    public List<Contact> getContactsForExport(String status, String startDate, String endDate) {
        List<Contact> allContacts = contactRepository.findAllByOrderByCreatedAtDesc();

        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            allContacts = allContacts.stream()
                    .filter(contact -> status.equals(contact.getStatus()))
                    .collect(Collectors.toList());
        }

        // TODO: Add date filtering logic for startDate and endDate

        return allContacts;
    }

    // Bulk status update
    public void bulkUpdateStatus(List<Long> contactIds, String status, String adminNotes) {
        for (Long id : contactIds) {
            contactRepository.findById(id).ifPresent(contact -> {
                contact.setStatus(status);
                contact.setReadByAdmin(true);
                contact.setReviewedAt(LocalDateTime.now());
                contactRepository.save(contact);

                System.out.println("🔄 Bulk update - Contact " + id + " set to " + status);
            });
        }
    }

    // Get contacts by status
    public List<Contact> getContactsByStatus(String status) {
        List<Contact> allContacts = contactRepository.findAll();
        return allContacts.stream()
                .filter(contact -> status.equals(contact.getStatus()))
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void autoArchiveReviewedSubmissions() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Contact> oldReviewedContacts = contactRepository
                .findByReadByAdminTrueAndArchivedFalseAndCreatedAtBefore(sevenDaysAgo);

        for (Contact contact : oldReviewedContacts) {
            contact.setArchived(true);
            contact.setReviewedAt(LocalDateTime.now());
            contactRepository.save(contact);
        }

        if (!oldReviewedContacts.isEmpty()) {
            System.out.println("Auto-archived " + oldReviewedContacts.size() + " reviewed submissions");
        }
    }

    @Scheduled(cron = "0 */30 * * * ?")
    public void disableEditingForOldSubmissions() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Contact> oldContacts = contactRepository
                .findByCanEditTrueAndCreatedAtBefore(oneHourAgo);

        for (Contact contact : oldContacts) {
            contact.setCanEdit(false);
            contactRepository.save(contact);
        }

        if (!oldContacts.isEmpty()) {
            System.out.println("Disabled editing for " + oldContacts.size() + " old contacts");
        }
    }
}