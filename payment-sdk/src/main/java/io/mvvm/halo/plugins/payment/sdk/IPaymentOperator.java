package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.exception.CloseException;
import io.mvvm.halo.plugins.payment.sdk.exception.RefundException;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

/**
 * 支付相关操作.
 *
 * @author: pan
 **/
public interface IPaymentOperator {
    /**
     * @return 支付类型
     */
    Ref type();

    /**
     * @return 支付方式的状态是否可用
     */
    boolean status();

    /**
     * 支付初始化配置
     */
    default Mono<Boolean> initConfig() {
        return Mono.empty();
    }

    /**
     * 创建支付订单
     */
    Mono<CreatePaymentResponse> create(CreatePaymentRequest request);

    /**
     * 获取订单
     */
    Mono<PaymentInfo> fetch(PaymentRequest request);

    /**
     * 取消订单
     */
    default Mono<PaymentResponse> cancel(PaymentRequest request) {
        return Mono.error(new CloseException("暂不支持取消订单", request));
    }

    /**
     * 退款
     */
    default Mono<PaymentResponse> refund(PaymentRequest request) {
        return Mono.error(new RefundException("暂不支持退款", request));
    }

    /**
     * 支付异步通知
     */
    Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request);

    /**
     * 退款异步通知
     */
    default Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        return Mono.empty();
    }

    void destroy();
}
