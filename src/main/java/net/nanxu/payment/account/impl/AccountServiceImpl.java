package net.nanxu.payment.account.impl;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.account.Account;
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
            .filter(accountSetting -> accountSetting.getEnabled()
                && null != accountSetting.getAccount())
            .switchIfEmpty(Mono.error(new AccountException("Account " + name + " is disabled")))
            .flatMap(accountSetting -> Mono.justOrEmpty(registry.getAccount(name))
                .switchIfEmpty(Mono.defer(() -> {
                    Account account = accountSetting.getAccount();
                    IPayment payment = paymentRegistry.get(account.getChannel());
                    return createAccount(payment, account);
                })));
    }

    @Override
    public Mono<IAccount> createAccount(IPayment payment, IAccount account) {
        return Mono.defer(() -> {
            if (null == payment) {
                return Mono.error(
                    new AccountException("Payment " + account.getChannel() + " is not registered"));
            }
            if (null == account) {
                return Mono.error(new AccountException("Account is null"));
            }
            if (creating.compareAndSet(false, true)) {
                return payment.createAccount(account)
                    .doOnNext(registry::register)
                    .doFinally(e -> creating.set(false));
            }
            return Mono.empty();
        }).repeatWhenEmpty(100, count -> count.delayElements(Duration.ofMillis(100)));
    }

}
