package net.nanxu.payment.service.impl;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import net.nanxu.payment.account.Account;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.registry.AccountRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.service.AccountService;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * AccountServiceImpl.
 *
 * @author: P
 **/
public class AccountServiceImpl implements AccountService {

    private final AccountRegistry registry;
    private final PaymentRegistry paymentRegistry;
    private final ReactiveExtensionClient client;

    private final AtomicBoolean creating = new AtomicBoolean(false);

    public AccountServiceImpl(PaymentRegistry paymentRegistry,
        ReactiveExtensionClient client) {
        this.registry = new AccountRegistry();
        this.paymentRegistry = paymentRegistry;
        this.client = client;
    }

    @Override
    public Mono<IAccount> getAccount(String name) {
        return Mono.defer(() -> {
            IAccount account = registry.getAccount(name);
            if (null != account) {
                return Mono.just(account);
            }
            if (creating.compareAndSet(false, true)) {
                return client.get(Account.class, name)
                    .flatMap(e -> paymentRegistry.get(e.getChannel()).createAccount(e))
                    .doOnNext(registry::register)
                    .doFinally(e -> creating.set(false));
            }
            return Mono.empty();
        }).repeatWhenEmpty(100, count -> count.delayElements(Duration.ofMillis(100)));
    }

}
