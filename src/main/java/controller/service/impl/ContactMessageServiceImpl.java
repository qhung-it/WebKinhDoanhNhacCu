package controller.service.impl;
import controller.service.intf.ContactMessageService;
import model.dao.impl.ContactMessageDAOImpl;
import model.dao.intf.ContactMessageDAO;
import model.entity.ContactMessage;
import java.util.List;
public class ContactMessageServiceImpl implements ContactMessageService {
    private final ContactMessageDAO dao;

    public ContactMessageServiceImpl() {
        this.dao = new ContactMessageDAOImpl();
    }

    public void create(ContactMessage x) {
        if (x == null || blank(x.getName()) || blank(x.getEmail()) || blank(x.getSubject()) || blank(x.getMessage())) {
            throw new IllegalArgumentException("Thiếu thông tin liên hệ");
        }
        if (!dao.save(x)) {
            throw new IllegalStateException("Không lưu được tin nhắn");
        }
    }

    public void update(ContactMessage x) {
        if (x == null || blank(x.getContactId())) {
            throw new IllegalArgumentException("Thiếu mã liên hệ");
        } if (!dao.update(x)) {
            throw new IllegalStateException("Không cập nhật được liên hệ");
        }
    }

    public void delete(String id) {
        if (blank(id) || !dao.delete(id)) {
            throw new IllegalArgumentException("Không tìm thấy liên hệ");
        }
    }

    public ContactMessage find(String id) {
        return blank(id) ? null : dao.findById(id); }

    public List<ContactMessage> list() {
        return dao.findAll(); }

    private boolean blank(String s) {
        return s == null || s.isBlank(); }
}
