package net.nanxu.payment.setting;

import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * SimplePaymentSettingManager.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class PaymentSettingServiceImpl implements PaymentSettingService {

    private final ReactiveExtensionClient client;

    private final AtomicReference<PaymentSetting.Basic> basicRef = new AtomicReference<>();
    private final AtomicReference<PaymentSetting.AccountSetting> accountRef = new AtomicReference<>();

    @Override
    public Mono<PaymentSetting.Basic> getBasicSetting() {
        PaymentSetting.Basic basic = basicRef.get();
        if (null != basic) {
            return Mono.just(basic);
        }
        return client.get(PaymentSetting.Basic.class, "basic").doOnNext(basicRef::set);
    }

    @Override
    public Mono<PaymentSetting.AccountSetting> getAccountSetting(String name) {
        PaymentSetting.AccountSetting accountSetting = accountRef.get();
        if (null != accountSetting) {
            return Mono.just(accountSetting);
        }
        return client.get(PaymentSetting.AccountSetting.class, name).doOnNext(accountRef::set);
    }

}
