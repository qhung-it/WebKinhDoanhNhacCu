package controller.service.intf;

import model.entity.ContactMessage;
import model.entity.NewsletterSubscription;
import model.entity.WishlistItem;
import java.util.List;

public interface UserFeatureService {
    List<WishlistItem> getWishlist(String userId);
    boolean addWishlistItem(String userId, String productId);
    boolean removeWishlistItem(String userId, String productId);
    boolean guiLienHe(ContactMessage message);
    boolean dangKyNhanTin(NewsletterSubscription subscription);
}
