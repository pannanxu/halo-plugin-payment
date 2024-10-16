package net.nanxu.payment.service;

import java.util.List;
import net.nanxu.payment.PaymentExtensionGetter;
import net.nanxu.payment.ReactiveExtensionClientTest;
import net.nanxu.payment.account.Account;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.registry.AccountRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.service.impl.AccountServiceImpl;
import net.nanxu.testplugin.WeChatPayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.halo.app.extension.ReactiveExtensionClient;

class AccountServiceTest {

    AccountService accountService;

    @BeforeEach
    void setUp() {
        AccountRegistry accountRegistry = new AccountRegistry();
        // accountRegistry.register(createMockAccount());

        PaymentExtensionGetter paymentExtensionGetter = Mockito.mock(PaymentExtensionGetter.class);
        Mockito.when(paymentExtensionGetter.getPaymentExtensions())
            .thenReturn(List.of(new WeChatPayment()));
        PaymentRegistry registry = new PaymentRegistry(paymentExtensionGetter);

        ReactiveExtensionClient client = new ReactiveExtensionClientTest();
        accountService = new AccountServiceImpl(registry, client);
    }

    @Test
    void getAccount() {
        Mono<IAccount> mono = accountService.getAccount("test").doOnNext(System.out::println);

        StepVerifier.create(mono)
            .expectNextCount(1)
            .verifyComplete();
    }

    Account createMockAccount() {
        Account account = new Account();
        account.setName("test");
        account.setChannel("WeChat");
        account.setMaster(true);
        return account;
    }
}