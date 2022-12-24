package io.mvvm.halo.plugins.payment.sdk.async;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * http path 路径寻找支付方式.
 *
 * @author: pan
 **/
@Component
public class PathVariableAsyncPayment extends AbstractAsyncPayment {

    public static final String name = "paymentType";

    private final PaymentDispatcher dispatcher;

    public PathVariableAsyncPayment(PaymentDispatcher dispatcher,
                                    AsyncNotifyManager asyncNotifyManager) {
        super(asyncNotifyManager);
        this.dispatcher = dispatcher;
    }

    @Override
    protected Mono<IPayment> dispatch(ServerRequest request) {
        return dispatcher.dispatch(request.pathVariable(name));
    }
}
