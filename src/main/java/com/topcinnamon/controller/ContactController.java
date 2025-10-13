package com.topcinnamon.controller;
import com.topcinnamon.dto.ContactRequest;
import com.topcinnamon.dto.EditContactRequest;
import com.topcinnamon.models.Contact;
import com.topcinnamon.service.ContactService;
import com.topcinnamon.service.EmailService; // ADD THIS IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactService contactService;

    @Autowired // ADD THIS
    private EmailService emailService; // ADD THIS

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
    // Test endpoint to verify controller is working
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("✅ ContactController is working! " + LocalDateTime.now());
    }
    // Test submission endpoint
    @PostMapping("/test-submission")
    public ResponseEntity<?> testSubmission(@RequestBody Map<String, String> testData) {
        try {
            System.out.println("🧪 Test submission received: " + testData);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test submission successful");
            response.put("receivedData", testData);
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Test failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    // Enhanced submission with immediate admin notification
    @PostMapping
    public ResponseEntity<?> submitContactForm(@Valid @RequestBody ContactRequest contactRequest, BindingResult bindingResult) {
        try {
            System.out.println("📨 Received contact form submission from: " + contactRequest.getEmail());

            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getFieldErrors().stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.joining(", "));

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Validation failed: " + errorMessage);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Create and save contact
            Contact contact = new Contact();
            contact.setName(contactRequest.getName());
            contact.setEmail(contactRequest.getEmail());
            contact.setPhone(contactRequest.getPhone());
            contact.setCountry(contactRequest.getCountry());
            contact.setSubject(contactRequest.getSubject());
            contact.setMessage(contactRequest.getMessage());
            contact.setStatus("Pending");

            Contact savedContact = contactService.saveContact(contact);

            // Send admin notification
            sendAdminNotification(savedContact);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Message sent successfully! You can edit within 1 hour.");
            response.put("submissionId", savedContact.getId());
            response.put("editToken", savedContact.getEditToken());
            response.put("editAllowedUntil", savedContact.getCreatedAt().plusHours(1));

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            System.err.println("❌ Error in submitContactForm: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error sending message: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private void sendAdminNotification(Contact contact) {
        String subject = "📧 New Contact Form Submission - " + contact.getName();
        String message = "New contact form submission received:\n\n" +
                "Name: " + contact.getName() + "\n" +
                "Email: " + contact.getEmail() + "\n" +
                "Phone: " + contact.getPhone() + "\n" +
                "Country: " + contact.getCountry() + "\n" +
                "Subject: " + contact.getSubject() + "\n" +
                "Message: " + contact.getMessage() + "\n\n" +
                "Submitted: " + contact.getCreatedAt() + "\n" +
                "Status: " + contact.getStatus() + "\n\n" +
                "Please review in admin dashboard.";

        // This would send to admin email - configure in application.properties
        emailService.sendAdminNotification(subject, message);
    }

    // Export functionality
    @GetMapping("/admin/export")
    public ResponseEntity<?> exportContacts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<Contact> contacts = contactService.getContactsForExport(status, startDate, endDate);

            // Convert to CSV/Excel format (simplified)
            List<Map<String, Object>> exportData = contacts.stream()
                    .map(contact -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("id", contact.getId());
                        row.put("name", contact.getName());
                        row.put("email", contact.getEmail());
                        row.put("phone", contact.getPhone());
                        row.put("country", contact.getCountry());
                        row.put("subject", contact.getSubject());
                        row.put("message", contact.getMessage());
                        row.put("status", contact.getStatus());
                        row.put("createdAt", contact.getCreatedAt());
                        row.put("reviewedAt", contact.getReviewedAt());
                        return row;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(exportData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error exporting contacts: " + e.getMessage());
        }
    }

    // Bulk status update
    @PostMapping("/admin/bulk-status")
    public ResponseEntity<?> bulkUpdateStatus(@RequestBody BulkStatusRequest request) {
        try {
            contactService.bulkUpdateStatus(request.getContactIds(), request.getStatus(), request.getAdminNotes());
            return ResponseEntity.ok().body("Status updated successfully for " + request.getContactIds().size() + " contacts");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating status: " + e.getMessage());
        }
    }



    @GetMapping("/admin/dashboard/stats")
    public ResponseEntity<?> getAdminDashboardStats() {
        try {
            Map<String, Object> stats = contactService.getAdminDashboardStats();
            return ResponseEntity.ok().body(stats);
        } catch (Exception e) {
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalMessages", 0);
            defaultStats.put("pendingMessages", 0);
            defaultStats.put("approvedMessages", 0);
            defaultStats.put("rejectedMessages", 0);
            defaultStats.put("recentSubmissions", 0);
            return ResponseEntity.ok().body(defaultStats);
        }
    }

    @GetMapping("/user-submissions")
    public ResponseEntity<?> getUserSubmissions(
            @RequestParam String email,
            @RequestParam String phone) {
        System.out.println("📋 Getting user submissions for: " + email);
        List<Contact> submissions = contactService.findPreviousSubmissions(email, phone);
        List<Map<String, Object>> response = submissions.stream()
                .map(sub -> {
                    Map<String, Object> subMap = new HashMap<>();
                    subMap.put("id", sub.getId());
                    subMap.put("subject", sub.getSubject());
                    subMap.put("message", sub.getMessage());
                    subMap.put("createdAt", sub.getCreatedAt());
                    subMap.put("status", sub.getStatus());
                    subMap.put("canEdit", sub.isEditingAllowed());
                    subMap.put("editToken", sub.getEditToken());
                    return subMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/edit/{editToken}")
    public ResponseEntity<?> getContactForEditing(@PathVariable String editToken) {
        System.out.println("🔍 Looking up contact with token: " + editToken);
        Optional<Contact> contact = contactService.getContactByEditToken(editToken);
        if (contact.isPresent()) {
            Contact contactObj = contact.get();
            if (!contactObj.isEditingAllowed()) {
                return ResponseEntity.badRequest().body("Editing time has expired. You can no longer edit this submission.");
            }
            return ResponseEntity.ok().body(contactObj);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editContact(
            @PathVariable Long id,
            @Valid @RequestBody EditContactRequest editRequest,
            BindingResult bindingResult) {
// Handle validation errors for edit
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Validation failed: " + errorMessage);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        Optional<Contact> existingContact = contactService.getContactById(id);
        if (existingContact.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Contact contact = existingContact.get();
        if (!contact.getEditToken().equals(editRequest.getEditToken())) {
            return ResponseEntity.badRequest().body("Invalid edit token");
        }
        if (!contact.isEditingAllowed()) {
            return ResponseEntity.badRequest().body("Editing time has expired. You can no longer edit this submission.");
        }
        contact.setName(editRequest.getName());
        contact.setEmail(editRequest.getEmail());
        contact.setPhone(editRequest.getPhone());
        contact.setCountry(editRequest.getCountry());
        contact.setSubject(editRequest.getSubject());
        contact.setMessage(editRequest.getMessage());
        Contact updatedContact = contactService.saveContact(contact);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Submission updated successfully!");
        response.put("submission", updatedContact);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping
    public List<Contact> getAllContacts() {
        return contactService.getAllContacts();
    }
    @GetMapping("/active")
    public List<Contact> getActiveSubmissions() {
        return contactService.getActiveSubmissions();
    }
    @GetMapping("/archived")
    public List<Contact> getArchivedSubmissions() {
        return contactService.getArchivedSubmissions();
    }
    @GetMapping("/all-ordered")
    public List<Contact> getAllSubmissionsOrdered() {
        return contactService.getAllContactsOrderedByDate();
    }
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        contactService.markAsRead(id);
        return ResponseEntity.ok().body("Marked as read");
    }
    @PutMapping("/{id}/update-status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
        try {
            String newStatus = statusRequest.get("status");

            // Validate allowed statuses
            if (!isValidContactStatus(newStatus)) {
                return ResponseEntity.badRequest().body("Invalid status. Allowed values: Pending, Approved, Rejected");
            }

            Optional<Contact> contactOpt = contactService.getContactById(id);
            if (contactOpt.isPresent()) {
                Contact contact = contactOpt.get();
                contact.setStatus(newStatus);

                // Auto-set readByAdmin for Approved/Rejected
                if ("Approved".equals(newStatus) || "Rejected".equals(newStatus)) {
                    contact.setReadByAdmin(true);
                    contact.setReviewedAt(LocalDateTime.now());
                }

                Contact updatedContact = contactService.saveContact(contact);
                return ResponseEntity.ok().body(updatedContact);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating status: " + e.getMessage());
        }
    }

    // Add this helper method
    private boolean isValidContactStatus(String status) {
        return status != null &&
                (status.equals("Pending") || status.equals("Approved") || status.equals("Rejected"));
    }


    @GetMapping("/{id}/can-edit")
    public ResponseEntity<?> checkEditStatus(@PathVariable Long id) {
        Optional<Contact> contact = contactService.getContactById(id);
        if (contact.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("canEdit", contact.get().isEditingAllowed());
            response.put("expiresAt", contact.get().getCreatedAt().plusHours(1));
            response.put("timeRemaining", contact.get().getTimeRemaining());
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.ok().body("Contact deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting contact: " + e.getMessage());
        }
    }
    @GetMapping("/submissions")
    public ResponseEntity<?> getUserSubmissionsByToken(@RequestParam String token) {
        try {
            System.out.println("🔍 Getting submissions for token: " + token);
            Optional<Contact> contactOpt = contactService.getContactByEditToken(token);
            if (contactOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Contact contact = contactOpt.get();
            List<Contact> userSubmissions = contactService.findPreviousSubmissions(
                    contact.getEmail(),
                    contact.getPhone()
            );
// Convert to response DTO
            List<Map<String, Object>> response = userSubmissions.stream()
                    .map(sub -> {
                        Map<String, Object> subMap = new HashMap<>();
                        subMap.put("id", sub.getId());
                        subMap.put("name", sub.getName());
                        subMap.put("email", sub.getEmail());
                        subMap.put("phone", sub.getPhone());
                        subMap.put("country", sub.getCountry());
                        subMap.put("subject", sub.getSubject());
                        subMap.put("message", sub.getMessage());
                        subMap.put("createdAt", sub.getCreatedAt());
                        subMap.put("status", sub.getStatus());
                        subMap.put("reviewed", sub.isReadByAdmin());
                        subMap.put("canEdit", sub.isEditingAllowed());
                        subMap.put("editToken", sub.getEditToken());
                        return subMap;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error loading submissions: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @PutMapping("/submissions/{id}/review")
    public ResponseEntity<?> markSubmissionAsReviewed(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            Optional<Contact> contactOpt = contactService.getContactByEditToken(token);
            if (contactOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid token");
            }
            Optional<Contact> submissionOpt = contactService.getContactById(id);
            if (submissionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Contact submission = submissionOpt.get();
// Verify ownership
            if (!submission.getEmail().equals(contactOpt.get().getEmail()) ||
                    !submission.getPhone().equals(contactOpt.get().getPhone())) {
                return ResponseEntity.badRequest().body("Access denied");
            }
            submission.setReadByAdmin(true);
            submission.setStatus("Reviewed");
            submission.setReviewedAt(LocalDateTime.now());
            Contact updatedContact = contactService.saveContact(submission);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Submission marked as reviewed");
            response.put("submission", updatedContact);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error updating submission: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/test-simple")
    public ResponseEntity<?> testSimpleSubmission(@RequestBody Map<String, Object> data) {
        try {
            System.out.println("🧪 SIMPLE TEST - Received data: " + data);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Simple test successful!");
            response.put("received", data);
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Test failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/submissions/{id}")
    public ResponseEntity<?> deleteUserSubmission(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Optional<Contact> contactOpt = contactService.getContactByEditToken(token);
            if (contactOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid token");
            }
            Optional<Contact> submissionOpt = contactService.getContactById(id);
            if (submissionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Contact submission = submissionOpt.get();
// Verify ownership
            if (!submission.getEmail().equals(contactOpt.get().getEmail()) ||
                    !submission.getPhone().equals(contactOpt.get().getPhone())) {
                return ResponseEntity.badRequest().body("Access denied");
            }
            contactService.deleteContact(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Submission deleted successfully");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting submission: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateContactStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusRequest) {
        try {
            String newStatus = statusRequest.get("status");
            String adminNotes = statusRequest.get("adminNotes");

            if (!isValidContactStatus(newStatus)) {
                return ResponseEntity.badRequest().body("Invalid status. Allowed values: Pending, Approved, Rejected");
            }

            Optional<Contact> contactOpt = contactService.getContactById(id);
            if (contactOpt.isPresent()) {
                Contact contact = contactOpt.get();
                contact.setStatus(newStatus);
                contact.setAdminNotes(adminNotes); // Save admin notes
                contact.setReadByAdmin(true);
                contact.setReviewedAt(LocalDateTime.now());

                Contact updatedContact = contactService.saveContact(contact);
                return ResponseEntity.ok().body(updatedContact);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating status: " + e.getMessage());
        }
    }

    public static class BulkStatusRequest {
        private List<Long> contactIds;
        private String status;
        private String adminNotes;

        // Getters and setters
        public List<Long> getContactIds() { return contactIds; }
        public void setContactIds(List<Long> contactIds) { this.contactIds = contactIds; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getAdminNotes() { return adminNotes; }
        public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> adminDeleteContact(@PathVariable Long id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.ok().body("Contact deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting contact: " + e.getMessage());
        }
    }

    @GetMapping("/admin/export/data")
    public ResponseEntity<?> getContactsExportData(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<Contact> contacts = contactService.getContactsForExport(status, startDate, endDate);

            // Convert to export-friendly format
            List<Map<String, Object>> exportData = contacts.stream().map(contact -> {
                Map<String, Object> contactData = new HashMap<>();
                contactData.put("id", contact.getId());
                contactData.put("name", contact.getName());
                contactData.put("email", contact.getEmail());
                contactData.put("phone", contact.getPhone());
                contactData.put("country", contact.getCountry());
                contactData.put("subject", contact.getSubject());
                contactData.put("message", contact.getMessage());
                contactData.put("status", contact.getStatus());
                contactData.put("createdAt", contact.getCreatedAt());
                contactData.put("reviewedAt", contact.getReviewedAt());
                contactData.put("readByAdmin", contact.isReadByAdmin());
                contactData.put("adminNotes", contact.getAdminNotes());
                return contactData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok().body(exportData);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching export data: " + e.getMessage());
        }
    }

}
