package model.dao.intf;

import model.entity.ContactMessage;
import java.util.List;

public interface ContactMessageDAO {
    boolean save(ContactMessage value);
    boolean update(ContactMessage value);
    boolean delete(String id);
    ContactMessage findById(String id);
    List<ContactMessage> findAll();
}
