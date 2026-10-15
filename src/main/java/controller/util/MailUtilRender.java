package controller.util;


import jakarta.mail.MessagingException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MailUtilRender {
    public static void sendMai(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException {
        try {
            String API_KEY = System.getenv("BREVO_API_KEY");

            String cleanFrom = from.trim();
            String cleanTo = to.trim();
            String cleanSubject = subject.replace("\"", "\\\"");
            String cleanBody = body.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "");

            String jsonPayload = "{"
                    + "\"sender\":{\"email\":\"" + cleanFrom + "\"},"
                    + "\"to\":[{\"email\":\"" + cleanTo + "\"}],"
                    + "\"subject\":\"" + cleanSubject + "\","
                    + (bodyIsHTML ? "\"htmlContent\":\"" : "\"textContent\":\"") + cleanBody + "\""
                    + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("API-KEY", API_KEY)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse  <String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new MessagingException("Lỗi Brevo API (Mã " + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            throw new MessagingException("Không thể gửi mail qua HTTP API: " + e.getMessage(), e);
        }
    }
}
