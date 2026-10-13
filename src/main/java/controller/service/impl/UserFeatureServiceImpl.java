package controller.service.impl;

import controller.service.intf.UserFeatureService;
import model.dao.impl.UserFeatureDAOImpl;
import model.dao.intf.UserFeatureDAO;
import model.entity.ContactMessage;
import model.entity.NewsletterSubscription;
import model.entity.WishlistItem;
import java.util.List;

public class UserFeatureServiceImpl implements UserFeatureService {
    private final UserFeatureDAO dao = new UserFeatureDAOImpl();

    @Override public List<WishlistItem> getWishlist(String userId) { return dao.findWishlistByUserId(userId); }
    @Override public boolean addWishlistItem(String userId, String productId) { return dao.addWishlistItem(userId, productId); }
    @Override public boolean removeWishlistItem(String userId, String productId) { return dao.removeWishlistItem(userId, productId); }
    @Override public boolean guiLienHe(ContactMessage message) { return dao.saveContact(message); }
    @Override public boolean dangKyNhanTin(NewsletterSubscription subscription) { return dao.subscribe(subscription); }
}
