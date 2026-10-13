package model.dao.intf;
import model.entity.SiteSetting;
public interface SiteSettingDAO { SiteSetting findDefault(); boolean saveOrUpdate(SiteSetting value); }
