package controller.service.impl;
import controller.service.intf.AdminAuditLogService;
import model.dao.impl.AdminAuditLogDAOImpl;
import model.dao.intf.AdminAuditLogDAO;
import model.entity.AdminAuditLog;
import java.time.LocalDateTime;
import java.util.List;
public class AdminAuditLogServiceImpl implements AdminAuditLogService {
    private final AdminAuditLogDAO dao;

    public AdminAuditLogServiceImpl() {
        this.dao = new AdminAuditLogDAOImpl();
    }

    public void record(AdminAuditLog x) {
        if (x == null || x.getAction() == null || x.getAction().isBlank()) {
            throw new IllegalArgumentException("Thiếu tên thao tác");
        }
        if (x.getCreatedAt() == null) {
            x.setCreatedAt(LocalDateTime.now());
        }
        if (!dao.save(x)) {
            throw new IllegalStateException("Không lưu được nhật ký");
        }
    }

    public List<AdminAuditLog> list() { return dao.findAll(); }
}
