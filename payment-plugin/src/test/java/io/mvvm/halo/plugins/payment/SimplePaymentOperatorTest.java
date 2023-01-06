package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

public class SimplePaymentOperatorTest implements IPaymentOperator {
    
    public static final String name = "simple-payment-test";

    @Override
    public run.halo.app.extension.Ref type() {
        return Ref.of(name);
    }

    @Override
    public boolean status() {
        return false;
    }

    @Override
    public Mono<CreatePaymentResponse> create(CreatePaymentRequest request) {
        CreatePaymentResponse response = new CreatePaymentResponse()
                .setTradeNo("123456")
                .setStatus(PaymentStatus.created)
                .setSuccess(true)
                .setOutTradeNo(request.getOutTradeNo())
                .setTotalFee(request.getTotalFee())
                .setExpand(request.getExpand())
                .setMode("url")
                .setModeData("https://www.baidu.com/payment.html");
        return Mono.just(response);
    }

    @Override
    public Mono<PaymentInfo> fetch(PaymentRequest request) {
        PaymentInfo response = new PaymentInfo()
                .setTradeNo("123456")
                .setStatus(PaymentStatus.created)
                .setSuccess(true)
                .setOutTradeNo(request.getOutTradeNo())
                .setTotalFee(1)
                .setExpand(request.getExpand());
        return Mono.just(response);
    }

    @Override
    public Mono<PaymentResponse> cancel(PaymentRequest request) {
        return IPaymentOperator.super.cancel(request);
    }

    @Override
    public Mono<PaymentResponse> refund(PaymentRequest request) {
        return IPaymentOperator.super.refund(request);
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return null;
    }

    @Override
    public void destroy() {

    }
}