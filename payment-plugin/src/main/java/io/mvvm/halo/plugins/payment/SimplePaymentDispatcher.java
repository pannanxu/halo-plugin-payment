package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PaymentWrapper. 支付调度
 *
 * @author: pan
 **/
@Slf4j
public class SimplePaymentDispatcher implements PaymentDispatcher {

    private final PaymentProvider provider;

    public SimplePaymentDispatcher(PaymentProvider provider) {
        this.provider = provider;
    }

    @Override
    public Mono<IPayment> dispatch(String payment) {
        return Mono.just(payment)
                .flatMap(provider::getPayment)
                .filterWhen(IPayment::status)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BaseException("暂不支持此支付"))));
    }

    @Override
    public Mono<IPayment> dispatch() {
        return Mono.deferContextual(ctx -> dispatch(ctx.get(PaymentDispatcher.payment).toString()));
    }

    @Override
    public Flux<IPayment> payments() {
        return provider.getPayments()
                .filterWhen(IPayment::status);
    }

    @Override
    public Flux<IPayment> payments(String device) {
        if (!StringUtils.hasLength(device)) {
            return payments();
        }
        return payments().collectList()
                .flatMapMany(list -> {
                    List<IPayment> payments = new ArrayList<>();
                    Map<String, List<IPayment>> groupName =
                            list.stream().collect(Collectors.groupingBy((e) -> e.getDescriptor().getName().split("-")[0]));
                    groupName.forEach((key, value) -> {
                        String name = key + "-" + device;
                        IPayment iPayment = value.stream().filter(e -> e.getDescriptor().getName().equals(name)).findFirst()
                                .orElseGet(() -> value.stream()
                                        .filter(x -> x.getDescriptor().getName().equals(key))
                                        .findFirst()
                                        .orElse(null));
                        payments.add(iPayment);
                    });
                    return Flux.fromStream(payments.stream());
                });
    }
}
