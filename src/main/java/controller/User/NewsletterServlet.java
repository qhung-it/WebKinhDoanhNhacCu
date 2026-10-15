package controller.User;

import controller.service.impl.UserFeatureServiceImpl;
import controller.util.MailUtilRender;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entity.NewsletterSubscription;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/newsletter")
public class NewsletterServlet extends jakarta.servlet.http.HttpServlet {
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
        String email = required(request, "email");
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            send(response, HttpServletResponse.SC_BAD_REQUEST, new ApiMessage("Email không hợp lệ"));
            return;
        }
        NewsletterSubscription subscription = new NewsletterSubscription();
        subscription.setSubscriptionId(UUID.randomUUID().toString());
        subscription.setEmail(email);
        subscription.setCreatedAt(LocalDateTime.now());
        if (!service.dangKyNhanTin(subscription)) {
            send(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new ApiMessage("Không thể đăng ký nhận tin"));
            return;
        }
        send(response, HttpServletResponse.SC_OK, new ApiMessage("Đăng ký nhận tin thành công"));
        String from = System.getenv("MAIL_USERNAME");
        String subject = "Welcome to SoundoraTeam!";
        String body = "Xin chào " + email + "!\n\n"
                + "🎉 Đăng ký thành công!\n\n"
                + "Cảm ơn bạn đã quan tâm chính sách của SoundoraTeam.\n\n"
                + "Bạn sẽ nhận được các thông báo mới nhất"
                + "về sản phẩm, chương trình khuyến mãi và những tin tức thú vị.\n\n"
                + "Hy vọng chúng tôi sẽ mang đến cho bạn những thông tin hữu ích \n\n"
                + "Cảm ơn bạn đã đồng hành!\n\n";
        boolean isBodyHTML = false;
        try {
            MailUtilRender.sendMai(email, from, subject, body, isBodyHTML);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
