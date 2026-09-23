import jakarta.persistence.EntityManagerFactory;
import model.util.JpaUtil;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Kết nối SQL Server...");

        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();

        System.out.println("Kết nối SQL Server thành công!");

        emf.close();
    }
}
