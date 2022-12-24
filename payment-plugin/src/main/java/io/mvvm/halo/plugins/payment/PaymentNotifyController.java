package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.async.PathVariableAsyncPayment;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * PaymentNotifyController.
 *
 * @author: pan
 **/
@RestController
@RequestMapping("/payment-code")
@AllArgsConstructor
public class PaymentNotifyController {

    private final PathVariableAsyncPayment asyncPayment;

    @RequestMapping("/notify/{name}/{paymentType}")
    public Mono<Object> codePaymentNotify(ServerRequest request,
                                          @PathVariable String name,
                                          @PathVariable String paymentType) {
        return asyncPayment.async(request);
    }

}
