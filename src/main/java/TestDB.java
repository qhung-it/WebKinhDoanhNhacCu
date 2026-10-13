import jakarta.persistence.EntityManagerFactory;
import model.util.JpaUtil;
import model.dao.impl.ProductDAOImpl;
import model.entity.Product;
import java.util.List;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Kết nối SQL Server...");

        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

        System.out.println("Kết nối SQL Server thành công!");

        try {
            List<Product> products = new ProductDAOImpl().findAll();
            System.out.println("Products read from DB: " + products.size());
            for (int i = 0; i < Math.min(products.size(), 5); i++) {
                Product product = products.get(i);
                System.out.println(product.getProductId() + " | " + product.getProductName()
                        + " | " + product.getPrice());
            }
        } finally {
            emf.close();
        }
    }
}
