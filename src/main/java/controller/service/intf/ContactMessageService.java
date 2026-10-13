package controller.service.intf;
import model.entity.ContactMessage;
import java.util.List;
public interface ContactMessageService { void create(ContactMessage value); void update(ContactMessage value); void delete(String id); ContactMessage find(String id); List<ContactMessage> list(); }
