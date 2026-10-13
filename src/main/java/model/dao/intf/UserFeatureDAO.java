package model.dao.intf;

import model.entity.ContactMessage;
import model.entity.NewsletterSubscription;
import model.entity.WishlistItem;
import java.util.List;

public interface UserFeatureDAO {
    List<WishlistItem> findWishlistByUserId(String userId);
    boolean addWishlistItem(String userId, String productId);
    boolean removeWishlistItem(String userId, String productId);
    boolean saveContact(ContactMessage message);
    boolean subscribe(NewsletterSubscription subscription);
}
