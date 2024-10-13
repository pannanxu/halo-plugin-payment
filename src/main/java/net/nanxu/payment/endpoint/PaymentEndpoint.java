package net.nanxu.payment.endpoint;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.Payment;
import net.nanxu.payment.PaymentOrder;
import net.nanxu.payment.core.IPayment;
import net.nanxu.payment.core.PaymentProfile;
import net.nanxu.payment.core.model.CallbackRequest;
import net.nanxu.payment.core.model.CallbackResult;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.theme.TemplateNameResolver;

/**
 * PaymentEndpoint.
 *
 * @author: P
 **/
@RequiredArgsConstructor
// @Component
public class PaymentEndpoint {
    private final TemplateNameResolver templateNameResolver;
    private final Payment payment;

    // @Bean
    public RouterFunction<ServerResponse> paymentRouter() {
        return route()
            // // 获取订单所有可以使用的支付渠道
            // .GET("/apis/payment/{orderId}/profile", this::payments, Builder::build)
            // 支付商回调通知
            .POST("/apis/payment/{orderId}/callback/{paymentType}", this::callback, Builder::build)
            // 渲染支付订单页面
            .GET("/payment/{orderId}", this::renderPaymentPage, Builder::build)
            .build();
    }

    // private Mono<ServerResponse> payments(ServerRequest request) {
    //     String orderId = request.queryParam("orderId").orElse(null);
    //     Flux<PaymentProfile> profiles = payment.getPaymentProfiles(PaymentOrder.builder()
    //         .userAgent(request.headers().firstHeader("User-Agent"))
    //         .request(request)
    //         .build());
    //     return ServerResponse.ok().body(profiles, PaymentProfile.class);
    // }

    Mono<ServerResponse> renderPaymentPage(ServerRequest request) {
        String orderId = request.pathVariable("orderId");
        Flux<PaymentProfile> profiles = payment.getPaymentProfiles(PaymentOrder.builder()
            .userAgent(request.headers().firstHeader("User-Agent"))
            .request(request)
            .build());

        return templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "payment")
            .flatMap(templateName -> profiles.collectList()
                .flatMap(e -> {
                    var model = new HashMap<String, Object>();
                    model.put("orderId", orderId);
                    model.put("profiles", e);
                    return ServerResponse.ok().render(templateName, model);
                }));
    }

    Mono<ServerResponse> callback(ServerRequest request) {
        return payment.getPayment(request.pathVariable("paymentType"))
            .map(IPayment::getCallback)
            .flatMap(callback -> callback.call(CallbackRequest.builder()
                .order(null)
                .request(request)
                .build()))
            .map(CallbackResult::getRender)
            .flatMap(render -> ServerResponse.ok().body(Mono.just(render), Object.class));
    }

}
