package model.dao.intf;
import model.entity.AdminAuditLog;
import java.util.List;
public interface AdminAuditLogDAO { boolean save(AdminAuditLog value); List<AdminAuditLog> findAll(); }
