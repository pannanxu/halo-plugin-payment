package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

/**
 * PaymentNotifyController.
 *
 * @author: pan
 **/
@Slf4j
@RestController
@RequestMapping("/apis/io.mvvm.halo.plugins.payment")
public class PaymentNotifyController {

    private final IAsyncPayment asyncPayment;
    private final PaymentDispatcher dispatcher;

    public PaymentNotifyController(IAsyncPayment asyncPayment, PaymentDispatcher dispatcher) {
        this.asyncPayment = asyncPayment;
        this.dispatcher = dispatcher;
        log.debug("PaymentNotifyController init.");
    }

    @RequestMapping("/notify/{gvk}/{name}/{paymentType}")
    public Mono<Object> codePaymentNotify(ServerRequest request,
                                          @PathVariable String gvk,
                                          @PathVariable String name,
                                          @PathVariable String paymentType) {
        return asyncPayment.paymentAsyncNotify(request, gvk, paymentType);
    }

    @GetMapping("/list/enabled")
    public Flux<Ref> paymentRefs() {
        return dispatcher.payments().map(IPayment::type);
    }

    @GetMapping("/init/{paymentType}")
    public Mono<Boolean> initPaymentConfig(@PathVariable String paymentType) {
        return dispatcher.dispatch(paymentType)
                .map(IPayment::getOperator)
                .flatMap(IPaymentOperator::initConfig);
    }

}
