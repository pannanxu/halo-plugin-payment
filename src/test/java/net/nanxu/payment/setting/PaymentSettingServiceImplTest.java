package net.nanxu.payment.setting;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.nanxu.payment.money.ISOCurrencyUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

@ExtendWith(MockitoExtension.class)
class PaymentSettingServiceImplTest {

    @Mock
    private ReactiveExtensionClient client;

    @InjectMocks
    private PaymentSettingServiceImpl manager;

    @Test
    void testGetBasicSettingWhenCacheIsEmpty() {
        PaymentSetting.Basic mockBasic = new PaymentSetting.Basic();
        mockBasic.setInternal("test");
        mockBasic.setCurrency(new ISOCurrencyUnit("USD", 840, "US Dollar", "$", 2));
        when(client.get(PaymentSetting.Basic.class, "basic")).thenReturn(Mono.just(mockBasic));

        Mono<PaymentSetting.Basic> result = manager.getBasicSetting();

        result.subscribe(basic -> {
            assertNotNull(basic);
            assertSame(mockBasic, basic);
        });

        verify(client, times(1)).get(PaymentSetting.Basic.class, "basic");
    }

}