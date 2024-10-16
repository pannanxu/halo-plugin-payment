package net.nanxu.payment.infra;

import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.infra.model.QueryRequest;
import net.nanxu.payment.infra.model.RefundRequest;
import reactor.core.publisher.Mono;

/**
 * 第三方支付插件通过上下文验证是否能够提供功能的支持.
 *
 * @author: P
 **/
public interface IPaymentSupport {

    default Mono<Boolean> isSupported(PaymentSupport request) {
        return Mono.just(Boolean.FALSE);
    }

    Mono<Boolean> pay(PaymentSupport request);

    Mono<Boolean> query(QueryRequest request);

    Mono<Boolean> refund(RefundRequest request);

    Mono<Boolean> cancel(RefundRequest request);

}
