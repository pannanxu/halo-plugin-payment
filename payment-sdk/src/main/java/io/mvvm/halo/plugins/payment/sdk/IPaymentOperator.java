package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.exception.CancelException;
import io.mvvm.halo.plugins.payment.sdk.exception.RefundException;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginWrapper;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * 支付相关操作.
 *
 * @author: pan
 **/
public interface IPaymentOperator extends ExtensionPoint {
    /**
     * @return 支付类型
     */
    PaymentDescriptor getDescriptor();

    PluginWrapper getPluginWrapper();

    /**
     * @return 支付方式的状态是否可用
     */
    boolean status();

    /**
     * 支付初始化配置
     */
    default Mono<Boolean> initConfig(ServerRequest request) {
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
        return Mono.error(new CancelException("暂不支持取消订单"));
    }

    /**
     * 退款
     */
    default Mono<RefundPaymentResponse> refund(RefundPaymentRequest request) {
        return Mono.error(new RefundException("暂不支持退款"));
    }

    /**
     * 获取退款状态
     */
    default Mono<RefundPaymentResponse> fetchRefund(FetchRefundPaymentRequest request) {
        return Mono.error(new RefundException("暂不支持查询退款状态"));
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

    /**
     * 当前 operator 被停止时出发
     */
    void destroy();
}
