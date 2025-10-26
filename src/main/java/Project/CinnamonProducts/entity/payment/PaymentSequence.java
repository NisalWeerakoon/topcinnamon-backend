package Project.CinnamonProducts.entity.payment;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_sequence")
public class PaymentSequence {

    @Id
    @Column(name = "id")
    private Long id = 1L;

    @Column(name = "last_number", nullable = false)
    private int lastNumber = 0;

    public PaymentSequence() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
    }
}




