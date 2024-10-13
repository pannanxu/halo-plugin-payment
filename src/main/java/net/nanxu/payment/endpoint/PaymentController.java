package net.nanxu.payment.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.order.Order;
import net.nanxu.payment.Payment;
import net.nanxu.payment.PaymentOrder;
import net.nanxu.payment.core.IPayment;
import net.nanxu.payment.core.PaymentProfile;
import net.nanxu.payment.core.model.CallbackRequest;
import net.nanxu.payment.core.model.CallbackResult;
import net.nanxu.payment.impl.WeChatPayment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ApiVersion;
import run.halo.app.theme.TemplateNameResolver;

/**
 * PaymentController.
 *
 * @author: pan
 **/
@ApiVersion("fake.halo.run/v1alpha1")
@RequestMapping("/payment")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final TemplateNameResolver templateNameResolver;
    private final Payment payment;

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("test");
    }

    // /apis/fake.halo.run/v1alpha1/payment/profiles?orderId=123
    @GetMapping("/profiles")
    public Flux<PaymentProfile> renderPaymentPage() {
        Flux<PaymentProfile> profiles = payment.getPaymentProfiles(PaymentOrder.builder()
            .userAgent("")
            .request(null)
            .order(new Order().setPayType(WeChatPayment.NAME))
            .build());

        return profiles;
        // return templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "payment")
        //     .flatMap(templateName -> profiles.collectList()
        //         .flatMap(e -> {
        //             var model = new HashMap<String, Object>();
        //             model.put("orderId", orderId);
        //             model.put("profiles", e);
        //             return ServerResponse.ok().render(templateName, model);
        //         }));
    }

    @PostMapping("/{orderId}/callback/{paymentType}")
    public Mono<Object> callback(@PathVariable String paymentType, @PathVariable String orderId,
        ServerRequest request) {
        return payment.getPayment(paymentType)
            .map(IPayment::getCallback)
            .flatMap(callback -> callback.call(CallbackRequest.builder()
                .order(null)
                .request(request)
                .build()))
            .map(CallbackResult::getRender);
    }

}
