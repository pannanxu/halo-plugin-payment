package io.mvvm.halo.plugins.payment;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * AsyncPayment.
 *
 * @author: pan
 **/
public interface IAsyncPayment {
    /**
     * 支付成功异步回调
     *
     * @param request     server request
     * @param gvk         gvk
     * @param paymentType payment
     * @return response
     */
    Mono<Object> paymentAsyncNotify(ServerRequest request, String gvk, String paymentType);

    /**
     * 退款成功异步回调
     *
     * @param request     server request
     * @param gvk         gvk
     * @param paymentType payment
     * @return response
     */
    Mono<Object> refundAsyncNotify(ServerRequest request, String gvk, String paymentType);

}
