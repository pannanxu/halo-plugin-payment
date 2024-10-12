package net.nanxu.payment.impl;

import net.nanxu.payment.PaymentOrder;
import net.nanxu.payment.core.AbstractPayment;
import net.nanxu.payment.core.IPaymentCallback;
import net.nanxu.payment.core.IPaymentSupport;
import net.nanxu.payment.core.PaymentProfile;
import net.nanxu.payment.core.model.CallbackRequest;
import net.nanxu.payment.core.model.CallbackResult;
import net.nanxu.payment.core.model.PaymentRequest;
import net.nanxu.payment.core.model.PaymentResult;
import net.nanxu.payment.core.model.QueryRequest;
import net.nanxu.payment.core.model.QueryResult;
import net.nanxu.payment.core.model.RefundRequest;
import net.nanxu.payment.core.model.RefundResult;
import reactor.core.publisher.Mono;

/**
 * AliPayment.
 *
 * @author: P
 **/
public class AliPayment extends AbstractPayment {

    public static final String NAME = "Ali";

    public AliPayment() {
        super(NAME,
                PaymentProfile.builder()
                        .name(NAME)
                        .displayName("支付宝")
                        .icon("ali.png")
                        .build(),
                new AliPaymentSupport(),
                new AliPaymentCallback());
    }

    @Override
    public Mono<PaymentResult> pay(PaymentRequest request) {
        return Mono.just(new PaymentResult());
    }

    @Override
    public Mono<QueryResult> query(QueryRequest request) {
        return null;
    }

    @Override
    public Mono<RefundResult> refund(RefundRequest request) {
        return null;
    }

    @Override
    public Mono<RefundResult> cancel(RefundRequest request) {
        return null;
    }


    public static class AliPaymentSupport implements IPaymentSupport {
        @Override
        public Mono<Boolean> pay(PaymentOrder request) {
            return Mono.just(request.getOrder().getPayType().equals(NAME));
        }

        @Override
        public Mono<Boolean> query(QueryRequest request) {
            return Mono.just(true);
        }

        @Override
        public Mono<Boolean> refund(RefundRequest request) {
            return Mono.just(true);

        }

        @Override
        public Mono<Boolean> cancel(RefundRequest request) {
            return Mono.just(true);
        }
    }

    public static class AliPaymentCallback implements IPaymentCallback {
        @Override
        public Mono<CallbackResult> call(CallbackRequest request) {
            return null;
        }
    }
}
