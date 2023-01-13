package io.mvvm.halo.plugins.payment.rule;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.cache.CacheManager;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.logging.Level;

/**
 * 缓存规则.
 *
 * @author: pan
 **/
@Slf4j
public class CacheRule extends BasePaymentRule {

    private final CacheManager cacheManager;

    public CacheRule(IPayment payment, CacheManager cacheManager) {
        super(payment);
        this.cacheManager = cacheManager;
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        String cacheKey = getDescriptor().getName() + "_" + request.getOutTradeNo();
        Optional<PaymentResponseWrapper<CreatePaymentResponse>> cacheResponse = cacheManager.get(cacheKey);
        return Mono.justOrEmpty(cacheResponse)
                .switchIfEmpty(Mono.defer(() -> super.create(request).map(response -> {
                    if (response.getResponse().isSuccess()) {
                        cacheManager.set(cacheKey, response, 10 * 60);
                    }
                    return response;
                })))
                .log("payment.rule.cache.create", log.isDebugEnabled() ? Level.INFO : Level.OFF);
    }
}
