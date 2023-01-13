package io.mvvm.halo.plugins.payment.rule;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 限流规则.
 *
 * @author: pan
 **/
@Slf4j
public class LimitRule extends BasePaymentRule {

    private final LimitRuleContext limitRuleContext;

    public LimitRule(IPayment payment, LimitRuleContext limitRuleContext) {
        super(payment);
        this.limitRuleContext = limitRuleContext;
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        return limit(request).flatMap(e -> super.create(request));
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        return limit(request).flatMap(e -> super.fetch(request));
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        return limit(request).flatMap(e -> super.cancel(request));
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request) {
        return limit(request).flatMap(e -> super.refund(request));
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request) {
        return limit(request).flatMap(e -> super.fetchRefund(request));
    }

    private Mono<Boolean> limit(PaymentRequest request) {
        return limitRuleContext.limit(request);
    }

}
