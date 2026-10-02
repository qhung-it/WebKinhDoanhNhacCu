package model.dao.impl;

import jakarta.persistence.EntityManager;
import model.dao.intf.CartDAO;
import model.entity.Cart;
import model.util.JpaUtil;

public class CartDAOImpl implements CartDAO {

    @Override
    public Cart findByUserId(String userId) {
        EntityManager em =
                JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                            "SELECT c FROM Cart c WHERE c.user.userId = :userId",
                            Cart.class
                    )
                    .setParameter("userId", userId)
                    .getSingleResult();

        } finally {
            em.close();
        }
    }
}