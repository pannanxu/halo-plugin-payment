package net.nanxu.payment;

import net.nanxu.payment.infra.model.PaymentRequest;
import net.nanxu.payment.infra.model.PaymentResult;
import net.nanxu.testplugin.AliPayment;
import net.nanxu.testplugin.WeChatPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * DispatcherPaymentTest.
 *
 * @author: P
 **/
public class DispatcherPaymentTest {

    private DispatcherPayment dispatcher;

    @BeforeEach
    public void setUp() {
        dispatcher = new DispatcherPayment();
        
        dispatcher.register(new WeChatPayment());
        dispatcher.register(new AliPayment());
    }

    @Test
    void getPayment_ExistingName_ShouldReturnPayment() throws InterruptedException {
        Mono<PaymentResult> resultMono = dispatcher.getPayment(WeChatPayment.NAME)
            .flatMap(payment -> payment.pay(new PaymentRequest()));

        StepVerifier.create(resultMono).expectNextCount(1).verifyComplete();
    }
    
}
