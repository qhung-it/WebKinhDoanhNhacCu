package controller.User;

import controller.service.impl.UserFeatureServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.ContactMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/contacts")
public class ContactServlet extends jakarta.servlet.http.HttpServlet {
    private static final com.google.gson.Gson JSON = new com.google.gson.Gson();

    protected void send(jakarta.servlet.http.HttpServletResponse response, int status, Object body)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSON.toJson(body, response.getWriter());
    }

    protected model.entity.User currentUser(jakarta.servlet.http.HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("user");
        return value instanceof model.entity.User ? (model.entity.User) value : null;
    }

    protected String required(jakarta.servlet.http.HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    protected void unauthorized(jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {
        send(response, jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED,
                new ApiMessage("Please log in"));
    }

    protected static final class ApiMessage {
        public final String message;
        ApiMessage(String message) { this.message = message; }
    }

    private final UserFeatureServiceImpl service = new UserFeatureServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = required(request, "name");
        String email = required(request, "email");
        String subject = required(request, "subject");
        String message = required(request, "message");
        if (name == null || email == null || subject == null || message == null
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Thông tin liên hệ không hợp lệ"));
            return;
        }
        ContactMessage contact = new ContactMessage();
        contact.setContactId(UUID.randomUUID().toString());
        contact.setFullName(name);
        contact.setEmail(email);
        contact.setPhone(request.getParameter("phone"));
        contact.setSubject(subject);
        contact.setMessage(message);
        contact.setCreatedAt(LocalDateTime.now());
        if (!service.guiLienHe(contact)) {
            send(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiMessage("Không thể lưu liên hệ"));
            return;
        }
        send(response, HttpServletResponse.SC_CREATED, new ApiMessage("Đã gửi liên hệ"));
    }
}
