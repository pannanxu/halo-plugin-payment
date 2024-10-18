package net.nanxu.payment.setting;

import reactor.core.publisher.Mono;

/**
 * PaymentSettingManager.
 *
 * @author: P
 **/
public interface PaymentSettingService {
    
    Mono<PaymentSetting.Basic> getBasicSetting();
    
    Mono<PaymentSetting.AccountSetting> getAccountSetting(String name);
    
}
