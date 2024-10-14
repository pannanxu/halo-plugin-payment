package net.nanxu.payment.infra;

import net.nanxu.payment.infra.model.PaymentRequest;
import net.nanxu.payment.infra.model.PaymentResult;
import net.nanxu.payment.infra.model.QueryRequest;
import net.nanxu.payment.infra.model.QueryResult;
import net.nanxu.payment.infra.model.RefundRequest;
import net.nanxu.payment.infra.model.RefundResult;
import reactor.core.publisher.Mono;

/**
 * IPayment.
 *
 * @author: P
 **/
public interface IPayment {

    String getName();

    PaymentProfile getProfile();

    IPaymentSupport getSupport();

    IPaymentCallback getCallback();

    Mono<PaymentResult> pay(PaymentRequest request);

    Mono<QueryResult> query(QueryRequest request);

    Mono<RefundResult> refund(RefundRequest request);

    Mono<RefundResult> cancel(RefundRequest request);

    /**
     * 注册时调用
     */
    default void register() {}

    /**
     * 注销时调用
     */
    default void unregister() {};

    /**
     * 去除一些不需要让第三方插件需要的功能
     */
    static IPayment wrap(IPayment payment) {
        return new IPayment() {
            @Override
            public String getName() {
                return payment.getName();
            }

            @Override
            public PaymentProfile getProfile() {
                return payment.getProfile();
            }

            @Override
            public IPaymentSupport getSupport() {
                return payment.getSupport();
            }

            @Override
            public IPaymentCallback getCallback() {
                throw new UnsupportedOperationException("Not support callback");
            }

            @Override
            public Mono<PaymentResult> pay(PaymentRequest request) {
                return payment.pay(request);
            }

            @Override
            public Mono<QueryResult> query(QueryRequest request) {
                return payment.query(request);
            }

            @Override
            public Mono<RefundResult> refund(RefundRequest request) {
                return payment.refund(request);
            }

            @Override
            public Mono<RefundResult> cancel(RefundRequest request) {
                return payment.cancel(request);
            }
        };
    }
}
