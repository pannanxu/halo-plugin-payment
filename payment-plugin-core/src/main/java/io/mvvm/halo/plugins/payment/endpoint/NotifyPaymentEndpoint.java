package io.mvvm.halo.plugins.payment.endpoint;

import io.mvvm.halo.plugins.payment.IAsyncPayment;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.GroupVersion;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * 异步通知接口.
 *
 * @author: pan
 **/
@Setter
@Component
public class NotifyPaymentEndpoint implements PaymentEndpoint {

    private IAsyncPayment asyncPayment;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return route(RequestPredicates.path("/payment/{token}/{gvk}/{name}/{paymentType}"), this::paymentAsyncNotify)
                .and(route(RequestPredicates.path("/refund/{token}/{gvk}/{name}/{refundNo}/{paymentType}"), this::refundAsyncNotify));
    }

    @Override
    public GroupVersion groupVersion() {
        // 这里的拼接的地址尽可能的短，避免部分第三方最长字符限制
        return new GroupVersion("payment", "notify");
    }

    Mono<ServerResponse> paymentAsyncNotify(ServerRequest request) {
        String gvk = request.pathVariable("gvk");
        String paymentType = request.pathVariable("paymentType");
        Mono<Object> response = asyncPayment.paymentAsyncNotify(request, gvk, paymentType);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response, Object.class);
    }

    Mono<ServerResponse> refundAsyncNotify(ServerRequest request) {
        String gvk = request.pathVariable("gvk");
        String paymentType = request.pathVariable("paymentType");
        Mono<Object> response = asyncPayment.refundAsyncNotify(request, gvk, paymentType);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response, Object.class);
    }

}
