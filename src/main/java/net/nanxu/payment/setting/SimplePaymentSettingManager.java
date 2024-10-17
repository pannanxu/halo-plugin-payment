package net.nanxu.payment.setting;

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
public class SimplePaymentSettingManager implements PaymentSettingManager {

    private final ReactiveExtensionClient client;

    @Override
    public Mono<PaymentSetting.Basic> getBasicSetting() {
        return client.get(PaymentSetting.Basic.class, "basic");
    }

    @Override
    public Mono<PaymentSetting.AccountSetting> getAccountSetting(String name) {
        return client.get(PaymentSetting.AccountSetting.class, name);
    }

}
