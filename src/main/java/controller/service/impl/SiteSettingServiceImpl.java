package controller.service.impl;
import controller.service.intf.SiteSettingService;
import model.dao.impl.SiteSettingDAOImpl;
import model.dao.intf.SiteSettingDAO;
import model.entity.SiteSetting;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class SiteSettingServiceImpl implements SiteSettingService {
    private final SiteSettingDAO dao;

    public SiteSettingServiceImpl() {
        this.dao = new SiteSettingDAOImpl();
    }

    public SiteSetting get() {
        SiteSetting x = dao.findDefault();
        if (x != null) return x; x = new SiteSetting();
        x.setSettingId("default"); return x;
    }

    public void save(SiteSetting x) {
        if (x == null || negative(x.getFreeShippingFrom()) || negative(x.getShippingFee()))
            throw new IllegalArgumentException("Phí vận chuyển và ngưỡng miễn phí không được âm");
        x.setSettingId("default"); x.setUpdatedAt(LocalDateTime.now()); if (!dao.saveOrUpdate(x))
            throw new IllegalStateException("Không lưu được cấu hình");
    }

    private boolean negative(BigDecimal x) {
        return x == null || x.signum() < 0;
    }
}
