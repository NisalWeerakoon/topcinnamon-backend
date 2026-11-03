package Project.CinnamonProducts.service.payment;

import Project.CinnamonProducts.entity.payment.PaymentSequence;
import Project.CinnamonProducts.repository.payment.PaymentSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentIdService {

    private final PaymentSequenceRepository sequenceRepository;

    public PaymentIdService(PaymentSequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    @Transactional
    public String nextPaymentId() {
        PaymentSequence sequence = sequenceRepository.findById(1L).orElseGet(() -> {
            PaymentSequence s = new PaymentSequence();
            return sequenceRepository.save(s);
        });

        int next = sequence.getLastNumber() + 1;
        if (next > 9999) {
            next = 1; // wrap around after 9999
        }
        sequence.setLastNumber(next);
        sequenceRepository.save(sequence);

        return String.format("%04d", next);
    }
    
    @Transactional
    public void resetSequence() {
        // Delete the existing sequence record
        sequenceRepository.deleteAll();
        
        // Create a new sequence starting from 0
        PaymentSequence newSequence = new PaymentSequence();
        newSequence.setId(1L);
        newSequence.setLastNumber(0);
        sequenceRepository.save(newSequence);
    }
}



