package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.ExpandConst;
import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.enums.DeviceType;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.utils.RandomUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * PaymentEndpoint.
 *
 * @author: pan
 **/
@Component
public class PaymentEndpoint {

    private final PaymentDispatcher dispatcher;
    private final IAsyncPayment asyncPayment;

    public PaymentEndpoint(PaymentDispatcher dispatcher, IAsyncPayment asyncPayment) {
        this.dispatcher = dispatcher;
        this.asyncPayment = asyncPayment;
    }

    /**
     * @return 启用的支付方式列表，如果指定了设备则根据规则返回符合此设备的模式
     */
    @Bean
    public RouterFunction<ServerResponse> enabledList() {
        return route(GET("/apis/io.mvvm.halo.plugins.payment/list/enabled"),
                request -> {
                    String device = request.queryParam("device").orElse(null);
                    Flux<PaymentDescriptor> descriptorFlux = dispatcher.payments(device).map(IPayment::getDescriptor);
                    return ServerResponse.ok().body(descriptorFlux, PaymentDescriptor.class);
                });
    }

    int x = 1;
    int y = 1;
    BiFunction<Integer, ArrayList, StringBuffer> castFunc = (i, map) -> ((StringBuffer)map.get(i));
    Map<Integer, Consumer<ArrayList>> applyFunc = Map.of(
            1, (map) -> castFunc.apply(y--, map).setCharAt(x, '.'),
            2, (map) -> castFunc.apply(x++, map).setCharAt(x, '.')
    );
    public void move(int dir, ArrayList map, String name) {
        applyFunc.get(dir).accept(map);
    }
    
    


    /**
     * @return 初始化一个支付模式
     */
    @Bean
    public RouterFunction<ServerResponse> initPaymentConfig() {
        return route(GET("/apis/io.mvvm.halo.plugins.payment/init/{name}"),
                request -> {
                    Mono<Boolean> resp = dispatcher.dispatch(request.pathVariable("name"))
                            .map(IPayment::getOperator)
                            .flatMap(IPaymentOperator::initConfig);
                    return ServerResponse.ok().body(resp, Boolean.class);
                });
    }

    /**
     * @return 测试使用，创建订单
     */
    @Bean
    public RouterFunction<ServerResponse> createPayment() {
        return route(GET("/apis/io.mvvm.halo.plugins.payment/create/{name}"),
                request -> {
                    CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
                    paymentRequest.setOutTradeNo(RandomUtils.createNonce(10));
                    paymentRequest.setTitle("测试商品");
                    paymentRequest.setDescription("商品说明");
                    paymentRequest.setClientIp("127.0.0.1");
                    paymentRequest.setDevice(DeviceType.pc.name());
                    paymentRequest.setTotalFee(1);
                    paymentRequest.setBackParams("1");
                    paymentRequest.setGvk("test");
                    Map<String, Object> map = new HashMap<>();
                    map.put(ExpandConst.returnUrl, "https://www.baidu.com");
                    paymentRequest.setExpand(map);
                    Mono<PaymentResponseWrapper<CreatePaymentResponse>> resp = dispatcher.dispatch(request.pathVariable("name"))
                            .flatMap(payment -> payment.create(paymentRequest));
                    return ServerResponse.ok().body(resp, PaymentResponseWrapper.class);
                });
    }

    /**
     * @return 测试使用，查询订单
     */
    @Bean
    public RouterFunction<ServerResponse> fetch() {
        return route(GET("/apis/io.mvvm.halo.plugins.payment/fetch/{name}"),
                request -> {
                    Mono<PaymentResponseWrapper<PaymentInfo>> resp = dispatcher.dispatch(request.pathVariable("name"))
                            .flatMap(payment -> payment.fetch(() -> request.queryParam("outTradeNo").orElse(null)));
                    return ServerResponse.ok().body(resp, PaymentResponseWrapper.class);
                });
    }

    @Bean
    public RouterFunction<ServerResponse> paymentNotify() {
        return route(GET("/apis/plugins.payment/notify/{gvk}/{name}/{paymentType}"),
                request -> {
                    String gvk = request.pathVariable("gvk");
                    String paymentType = request.pathVariable("paymentType");
                    Mono<Object> response = asyncPayment.paymentAsyncNotify(request, gvk, paymentType);
                    return ServerResponse.ok().body(response, Object.class);
                });
    }

}
