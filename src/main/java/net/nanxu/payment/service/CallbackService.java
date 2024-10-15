package net.nanxu.payment.service;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * IPaymentCallbackService.
 *
 * @author: P
 **/
public interface CallbackService {
    Mono<Object> callback(String channel, String orderNo, ServerRequest request);
}
