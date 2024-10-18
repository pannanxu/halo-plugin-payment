package net.nanxu.payment.account.impl;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.account.AccountService;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.channel.IPayment;
import net.nanxu.payment.channel.PaymentRegistry;
import net.nanxu.payment.exception.AccountException;
import net.nanxu.payment.setting.PaymentSettingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * AccountServiceImpl.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRegistry registry = new AccountRegistry();
    private final AtomicBoolean creating = new AtomicBoolean(false);

    private final PaymentRegistry paymentRegistry;
    private final PaymentSettingService settingService;


    @Override
    public Mono<IAccount> getAccount(String name) {
        return settingService.getAccountSetting(name)
            .flatMap(accountSetting -> {
                if (!accountSetting.getEnabled() || null == accountSetting.getAccount()) {
                    return Mono.error(new AccountException("Account " + name + " is disabled"));
                }
                IAccount account = registry.getAccount(name);
                if (null != account) {
                    return Mono.just(account);
                }
                return createAccount(paymentRegistry.get(accountSetting.getAccount().getChannel()), accountSetting.getAccount());
            });
    }

    @Override
    public Mono<IAccount> createAccount(IPayment payment, IAccount account) {
        if (null == payment) {
            return Mono.error(new AccountException("Payment " + account.getChannel() + " is not registered"));
        }
        if (null == account) {
            return Mono.error(new AccountException("Account is null"));
        }
        return Mono.defer(() -> {
            if (creating.compareAndSet(false, true)) {
                return payment.createAccount(account)
                    .doOnNext(registry::register)
                    .doFinally(e -> creating.set(false));
            }
            return Mono.empty();
        }).repeatWhenEmpty(100, count -> count.delayElements(Duration.ofMillis(100)));
    }

}
