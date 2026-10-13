package model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_message")
public class ContactMessage {
    @Id
    @Column(name = "contact_id")
    private String contactId;

    @Nationalized
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Nationalized
    @Column(name = "subject", nullable = false)
    private String subject;

    @Nationalized
    @Column(name = "message", nullable = false, length = 4000)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public String getContactId() {
        return contactId;
    }
    
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
