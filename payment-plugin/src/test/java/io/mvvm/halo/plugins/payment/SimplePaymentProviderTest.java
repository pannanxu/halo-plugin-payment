package io.mvvm.halo.plugins.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import run.halo.app.infra.ExternalUrlSupplier;

import java.net.URISyntaxException;

@ExtendWith(MockitoExtension.class)
public class SimplePaymentProviderTest {

    @Mock
    private ExternalUrlSupplier externalUrlSupplier;

    @InjectMocks
    private SimplePaymentProvider provider;

    SimplePaymentOperatorTest operator = new SimplePaymentOperatorTest();

    @BeforeEach
    void setUp() throws URISyntaxException {
//        Mockito.when(externalUrlSupplier.get()).thenReturn(URI.create("https://mvvm.io"));
    }

    @Test
    void register() {
        provider.register(operator);
    }

    @Test
    void unregister() {
        provider.register(operator);

        provider.unregister(operator);
    }

    @Test
    void getPayment() {
        provider.register(operator);
        provider.getPayment(SimplePaymentOperatorTest.name)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getPayments() {
        provider.register(operator);
        provider.getPayments()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
}