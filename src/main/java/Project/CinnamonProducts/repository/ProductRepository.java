package Project.CinnamonProducts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Project.CinnamonProducts.model.Product;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
}
