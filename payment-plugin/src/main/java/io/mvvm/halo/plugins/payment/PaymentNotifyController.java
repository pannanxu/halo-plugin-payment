//package io.mvvm.halo.plugins.payment;
//
//import io.mvvm.halo.plugins.payment.sdk.IPayment;
//import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import run.halo.app.extension.Ref;
//
///**
// * PaymentNotifyController.
// *
// * @author: pan
// **/
//@RestController
//@RequestMapping("/payment")
//@AllArgsConstructor
//public class PaymentNotifyController {
//
//    private final IAsyncPayment asyncPayment;
//    private final PaymentDispatcher dispatcher;
//
//    @RequestMapping("/notify/{gvk}/{name}/{paymentType}")
//    public Mono<Object> codePaymentNotify(ServerRequest request,
//                                          @PathVariable String gvk,
//                                          @PathVariable String name,
//                                          @PathVariable String paymentType) {
//        return asyncPayment.async(request, gvk, paymentType);
//    }
//
//    @RequestMapping("/types")
//    public Flux<Ref> paymentRefs() {
//        return dispatcher.payments().map(IPayment::type);
//    }
//
//}
