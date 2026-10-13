package model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contact_message")
public class ContactMessage {
    @Id
    @Column(name = "contact_id", length = 36)
    private String contactId = UUID.randomUUID().toString();

    @Nationalized
    @Column(name = "full_name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Nationalized
    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Nationalized
    @Lob @Column(name = "message", nullable = false)
    private String message;

    @Nationalized
    @Column(name = "status", nullable = false, length = 30)
    private String status = "Mới";

    @Nationalized @Lob
    @Column(name = "internal_note")
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return name;
    }

    public void setFullName(String fullName) {
        this.name = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
