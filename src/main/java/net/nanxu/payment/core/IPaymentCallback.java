package net.nanxu.payment.core;

import net.nanxu.payment.core.model.CallbackRequest;
import net.nanxu.payment.core.model.CallbackResult;
import reactor.core.publisher.Mono;

/**
 * PaymentCallback.
 *
 * @author: P
 **/
public interface IPaymentCallback {

    Mono<CallbackResult> call(CallbackRequest request);

}
