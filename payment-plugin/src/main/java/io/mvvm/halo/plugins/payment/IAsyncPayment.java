package io.mvvm.halo.plugins.payment;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * AsyncPayment.
 *
 * @author: pan
 **/
public interface IAsyncPayment {

     Mono<Object> async(ServerRequest request, String gvk, String paymentType);
}
