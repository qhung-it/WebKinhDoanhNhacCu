package model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "site_content")
public class SiteContent {
    @Id
    @Column(name = "content_key", length = 100, nullable = false)
    private String contentKey;

    @Lob
    @Column(name = "json_data", nullable = false)
    private String jsonData;

    protected SiteContent() {
    }

    public String getContentKey() {
        return contentKey;
    }

    public String getJsonData() {
        return jsonData;
    }
}
