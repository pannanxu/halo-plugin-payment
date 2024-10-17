package net.nanxu.payment.channel;

import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import reactor.core.publisher.Mono;

/**
 * PaymentService.
 *
 * @author: P
 **/
public interface PaymentService {

    Mono<PaymentResult> pay(PaymentRequest request);

    Mono<QueryResult> query(QueryRequest request);

    Mono<RefundResult> refund(RefundRequest request);

    Mono<RefundResult> cancel(RefundRequest request);

}
