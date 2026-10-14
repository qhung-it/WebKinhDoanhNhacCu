package model.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JpaUtil {

    private static final EntityManagerFactory emf = createEntityManagerFactory();

    private static EntityManagerFactory createEntityManagerFactory() {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        // Keep the persistence.xml local defaults when no deployment variables are set.
        if (url == null && user == null && password == null) {
            return Persistence.createEntityManagerFactory("WebNhac");
        }

        if (url == null || url.isBlank() || user == null || user.isBlank() || password == null) {
            throw new IllegalStateException(
                    "Set all database environment variables: DB_URL, DB_USER, and DB_PASSWORD.");
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
        properties.put("jakarta.persistence.jdbc.url", url.trim());
        properties.put("jakarta.persistence.jdbc.user", user.trim());
        properties.put("jakarta.persistence.jdbc.password", password);

        return Persistence.createEntityManagerFactory("WebNhac", properties);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
