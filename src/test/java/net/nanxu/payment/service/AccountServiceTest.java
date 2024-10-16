package net.nanxu.payment.service;

import java.util.List;
import net.nanxu.payment.PaymentExtensionGetter;
import net.nanxu.payment.ReactiveExtensionClientTest;
import net.nanxu.payment.account.IAccount;
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
        PaymentExtensionGetter getter = Mockito.mock(PaymentExtensionGetter.class);
        Mockito.when(getter.getPaymentExtensions()).thenReturn(List.of(new WeChatPayment()));

        PaymentRegistry registry = new PaymentRegistry(getter);

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

}