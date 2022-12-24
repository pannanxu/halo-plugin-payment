package io.mvvm.halo.plugins.payment.sdk.async;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * 指定名称方式寻找支付方式.
 *
 * @author: pan
 **/
public abstract class NamedAsyncPayment extends AbstractAsyncPayment {

    private final PaymentDispatcher dispatcher;

    public NamedAsyncPayment(PaymentDispatcher dispatcher,
                             AsyncNotifyManager asyncNotifyManager) {
        super(asyncNotifyManager);
        this.dispatcher = dispatcher;
    }

    protected abstract String named();

    @Override
    protected Mono<IPayment> dispatch(ServerRequest request) {
        return dispatcher.dispatch(named());
    }
}
