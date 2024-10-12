package net.nanxu.payment.core;

import net.nanxu.payment.PaymentOrder;
import net.nanxu.payment.core.model.QueryRequest;
import net.nanxu.payment.core.model.RefundRequest;
import reactor.core.publisher.Mono;

/**
 * IPaymentSupport.
 *
 * @author: P
 **/
public interface IPaymentSupport {

    Mono<Boolean> pay(PaymentOrder request);

    Mono<Boolean> query(QueryRequest request);

    Mono<Boolean> refund(RefundRequest request);

    Mono<Boolean> cancel(RefundRequest request);

}
