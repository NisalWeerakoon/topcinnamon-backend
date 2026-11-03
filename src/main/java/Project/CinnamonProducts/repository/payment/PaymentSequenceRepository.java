package Project.CinnamonProducts.repository.payment;

import Project.CinnamonProducts.entity.payment.PaymentSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentSequenceRepository extends JpaRepository<PaymentSequence, Long> {
}




