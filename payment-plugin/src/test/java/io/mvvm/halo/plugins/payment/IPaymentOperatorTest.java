package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import run.halo.app.extension.Ref;

class IPaymentOperatorTest {

    IPaymentOperator operator = new SimplePaymentOperatorTest();

    @Test
    void type() {
        Ref type = operator.type();
        Assertions.assertNotNull(type);
        Assertions.assertEquals("simple-payment-test", type.getName());
    }

    @Test
    void create() {
        CreatePaymentRequest request = new CreatePaymentRequest()
                .setOutTradeNo("2022122910420001")
                .setTitle("test payment title")
                .setGvk("test-payment")
                .setTotalFee(1)
                .setClientIp("127.0.0.1")
                .setDevice("pc");
        operator.create(request)
                .as(StepVerifier::create)
                .consumeNextWith(response -> {
                    Assertions.assertTrue(response.isSuccess());
                    Assertions.assertEquals(request.getOutTradeNo(), response.getOutTradeNo());
                    Assertions.assertEquals(request.getTotalFee(), response.getTotalFee());
                })
                .verifyComplete();
    }

    @Test
    void fetch() {
        PaymentRequest request = new PaymentRequest.SimplePaymentRequest()
                .setOutTradeNo("2022122910420001");
        operator.fetch(request)
                .as(StepVerifier::create)
                .consumeNextWith(response -> {
                    Assertions.assertTrue(response.isSuccess());
                    Assertions.assertEquals(request.getOutTradeNo(), response.getOutTradeNo());
                })
                .verifyComplete();
    }

    @Test
    void cancel() {
    }

    @Test
    void refund() {
    }

    @Test
    void asyncNotify() {
    }

    @Test
    void destroy() {
    }
}