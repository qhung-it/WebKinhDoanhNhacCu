package controller.service.intf;
import model.entity.InventoryHistory;
import java.util.List;
public interface InventoryHistoryService { void record(InventoryHistory value); List<InventoryHistory> list(); }
