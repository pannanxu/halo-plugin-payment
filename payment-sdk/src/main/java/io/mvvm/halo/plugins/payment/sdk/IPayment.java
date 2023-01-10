package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * IPayment.
 *
 * @author: pan
 **/
public interface IPayment {
    /**
     * @return 支付 extension 信息
     */
    PaymentDescriptor getDescriptor();

    /**
     * @return 支付方式的状态是否可用
     */
    boolean status();

    /**
     * 创建支付订单
     */
    Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request);

    /**
     * 查询支付订单信息
     */
    Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request);

    /**
     * 取消支付订单
     */
    Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request);

    /**
     * 支付订单退款.
     * <p>
     * 调用者需注意: 第三方退款存在同步或者异步的情况，
     * <p>
     * 如何判断是同步退款？
     * <p>
     * success == true && status == PaymentStatus.refund_successful
     * <p>
     * 如何判断是异步退款？
     * success == true && status == PaymentStatus.refund_processing
     */
    Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request);

    /**
     * 获取退款订单
     */
    Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request);

    /**
     * 支付异步通知
     */
    default Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return Mono.empty();
    }

    /**
     * 退款异步通知
     */
    default Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        return Mono.empty();
    }


}
