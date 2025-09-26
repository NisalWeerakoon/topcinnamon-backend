package Project.CinnamonProducts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Project.CinnamonProducts.models.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
