package net.nanxu.payment.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.PaymentFactory;
import net.nanxu.payment.channel.CallbackService;
import net.nanxu.payment.channel.model.CallbackRequest;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.order.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import run.halo.app.theme.TemplateNameResolver;

/**
 * PaymentController.
 *
 * @author: pan
 **/
// @ApiVersion("fake.halo.run/v1alpha1")
@RequestMapping("/payment")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final TemplateNameResolver templateNameResolver;
    private final PaymentFactory payment;

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("test");
    }

    @PostMapping("/{orderNo}/pay/{channel}")
    public Mono<PaymentResult> pay(@PathVariable String orderNo, @PathVariable String channel) {
        return payment.getServiceFactory().getPayment().pay(new PaymentRequest());
    }

    @GetMapping("/{orderNo}/pay/status")
    public Mono<Order.PayStatus> payStatus(@PathVariable String orderNo) {
        return payment.getServiceFactory().getOrder().getOrder(orderNo)
            .map(Order::getPayStatus);
    }

    @PostMapping("/{internal}/{orderNo}/callback/{channel}")
    public Mono<Object> callback(
        @PathVariable String internal,
        @PathVariable String channel,
        @PathVariable String orderNo,
        @RequestBody String body) {
        CallbackService callback = payment.getServiceFactory().getCallback();
        return callback.validateInternal(internal)
            .flatMap(e -> {
                if (!e) {
                    // 随便返回一个
                    return Mono.just("success");
                }
                return callback.callback(
                    CallbackRequest.builder().channel(channel).orderNo(orderNo).requestBody(body).build());
            });
    }

}
