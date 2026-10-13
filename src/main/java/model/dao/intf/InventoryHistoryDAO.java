package model.dao.intf;
import model.entity.InventoryHistory;
import java.util.List;
public interface InventoryHistoryDAO { boolean save(InventoryHistory value); List<InventoryHistory> findAll(); }
