package io.mvvm.halo.plugins.payment.biz;

import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.mvvm.halo.plugins.payment.sdk.ExpandConst;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.SdkContextHolder;
import io.mvvm.halo.plugins.payment.sdk.enums.DeviceType;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.utils.RandomUtils;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.GroupVersion;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * InitPaymentEndpoint.
 *
 * @author: pan
 **/
@Setter
@Component
public class TestPaymentEndpoint {

    private  PaymentDispatcher dispatcher;

    public TestPaymentEndpoint() {
        this.dispatcher = SdkContextHolder.holder().getDispatcher();
    }

    @Bean
    public RouterFunction<ServerResponse> endpoint() {
        return route(GET("/apis/create/{name}"), this::create)
                .and(route(GET("/apis/fetch/{name}"), this::fetch))
                .and(route(GET("/apis/refund/{name}"), this::refund))
                .and(route(GET("/apis/cancel/{name}"), this::cancel))
                .and(route(GET("/apis/fetchRefund/{name}"), this::fetchRefund))
                ;
    }

    public GroupVersion groupVersion() {
        return new GroupVersion("io.mvvm.halo.plugins.payment.test", "v1");
    }

    Mono<ServerResponse> create(ServerRequest request) {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setOutTradeNo(RandomUtils.createNonce(10));
        paymentRequest.setTitle("测试商品");
        paymentRequest.setDescription("商品说明");
        paymentRequest.setClientIp("127.0.0.1");
        paymentRequest.setDevice(DeviceType.pc.name());
        paymentRequest.setMoney(Amount.of(1));
        paymentRequest.setBackParams("1");
        paymentRequest.setGvk(ExampleNotifyCallback.gvk);
        Map<String, Object> map = new HashMap<>();
        map.put(ExpandConst.returnUrl, "https://www.baidu.com");
        map.put(ExpandConst.limitRuleKey, "localhost");
        paymentRequest.setExpand(map);
        Mono<PaymentResponseWrapper<CreatePaymentResponse>> resp = dispatcher.dispatch(request.pathVariable("name"))
                .flatMap(payment -> payment.create(paymentRequest));
        return ServerResponse.ok().body(resp, PaymentResponseWrapper.class);
    }

    Mono<ServerResponse> fetch(ServerRequest request) {
        Mono<PaymentResponseWrapper<PaymentInfo>> resp = dispatcher.dispatch(request.pathVariable("name"))
                .flatMap(payment -> payment.fetch(() -> request.queryParam("outTradeNo").orElse(null)));
        return ServerResponse.ok().body(resp, PaymentResponseWrapper.class);
    }

    Mono<ServerResponse> cancel(ServerRequest request) {
        Mono<PaymentResponseWrapper<PaymentResponse>> resp = dispatcher.dispatch(request.pathVariable("name"))
                .flatMap(payment -> payment.cancel(() -> request.queryParam("outTradeNo").orElse(null)));
        return ServerResponse.ok().body(resp, PaymentResponseWrapper.class);
    }

    Mono<ServerResponse> refund(ServerRequest request) {
        RefundPaymentRequest refundPaymentRequest = new RefundPaymentRequest();
        refundPaymentRequest.setOutTradeNo(request.queryParam("outTradeNo").orElse(null));
        refundPaymentRequest.setRefundNo(request.queryParam("outTradeNo").orElse(null));
        refundPaymentRequest.setRefundMoney(Amount.ofYuan(request.queryParam("amount").orElse("1")));
        refundPaymentRequest.setGvk(ExampleNotifyCallback.gvk);
        Mono<PaymentResponseWrapper<RefundPaymentResponse>> resp = dispatcher.dispatch(request.pathVariable("name"))
                .flatMap(payment -> payment.refund(refundPaymentRequest));
        return ServerResponse.ok().body(resp, PaymentResponseWrapper.class);
    }

    Mono<ServerResponse> fetchRefund(ServerRequest request) {
        FetchRefundPaymentRequest fetchRefundPaymentRequest = new FetchRefundPaymentRequest();
        fetchRefundPaymentRequest.setOutTradeNo(request.queryParam("outTradeNo").orElse(null));
        fetchRefundPaymentRequest.setRefundNo(request.queryParam("outTradeNo").orElse(null));
        Mono<PaymentResponseWrapper<RefundPaymentResponse>> resp = dispatcher.dispatch(request.pathVariable("name"))
                .flatMap(payment -> payment.fetchRefund(fetchRefundPaymentRequest));
        return ServerResponse.ok().body(resp, PaymentResponseWrapper.class);
    }

}
