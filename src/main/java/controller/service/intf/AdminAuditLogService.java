package controller.service.intf;
import model.entity.AdminAuditLog;
import java.util.List;
public interface AdminAuditLogService { void record(AdminAuditLog value); List<AdminAuditLog> list(); }
