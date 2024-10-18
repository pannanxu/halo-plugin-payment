package net.nanxu.payment.channel;

import net.nanxu.payment.channel.model.CallbackRequest;
import reactor.core.publisher.Mono;

/**
 * IPaymentCallbackService.
 *
 * @author: P
 **/
public interface CallbackService {
    
    Mono<Boolean> validateInternal(String internal);
    
    Mono<Object> callback(CallbackRequest request);
}
