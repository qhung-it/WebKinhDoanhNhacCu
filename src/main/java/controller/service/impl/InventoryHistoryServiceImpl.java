package controller.service.impl;
import controller.service.intf.InventoryHistoryService;
import model.dao.impl.InventoryHistoryDAOImpl;
import model.dao.intf.InventoryHistoryDAO;
import model.entity.InventoryHistory;
import java.time.LocalDateTime;
import java.util.List;
public class InventoryHistoryServiceImpl implements InventoryHistoryService {
    private final InventoryHistoryDAO dao;

    public InventoryHistoryServiceImpl() {
        this.dao = new InventoryHistoryDAOImpl();
    }

    public void record(InventoryHistory x) {
        if (x == null || x.getProduct() == null || x.getOperation() == null || x.getOperation().isBlank()) {
            throw new IllegalArgumentException("Thiếu dữ liệu lịch sử kho");
        }
        if (x.getChangedAt() == null) {
            x.setChangedAt(LocalDateTime.now());
        }
        if (!dao.save(x)) {
            throw new IllegalStateException("Không lưu được lịch sử kho");
        }
    }

    public List<InventoryHistory> list() {
        return dao.findAll();
    }
}
