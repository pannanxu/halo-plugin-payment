package net.nanxu.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * DispatcherPaymentTest.
 *
 * @author: P
 **/
public class DispatcherPaymentTest {

    private PaymentFactory factory;

    @BeforeEach
    public void setUp() {
        // factory = new PaymentFactory(null, null, null);
    }

    @Test
    void getPayment_ExistingName_ShouldReturnPayment() throws InterruptedException {
        // Mono<PaymentResult> resultMono = factory.getPayment(WeChatPayment.NAME)
        //     .flatMap(payment -> payment.pay(new PaymentRequest()));
        //
        // StepVerifier.create(resultMono).expectNextCount(1).verifyComplete();
    }
    
}
