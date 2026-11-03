package Project.CinnamonProducts.repository;

import Project.CinnamonProducts.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    
    List<CartEntity> findByUserEmail(String userEmail);
    
    List<CartEntity> findAll();
    
    Optional<CartEntity> findByUserEmailAndProductIdAndOrderType(String userEmail, Long productId, String orderType);
    
    void deleteByUserEmail(String userEmail);
    
    void deleteByUserEmailAndProductIdAndOrderType(String userEmail, Long productId, String orderType);
    
    long countByUserEmail(String userEmail);
    
    long count();
}

