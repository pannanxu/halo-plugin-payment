package net.nanxu.payment.account.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.nanxu.payment.account.Account;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.account.TestChannelAccount;
import net.nanxu.payment.channel.IPayment;
import net.nanxu.payment.channel.PaymentRegistry;
import net.nanxu.payment.setting.PaymentSetting;
import net.nanxu.payment.setting.PaymentSettingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
@ExtendWith(MockitoExtension.class) 
class AccountServiceImplTest {

    @Mock
    PaymentRegistry paymentRegistry;
    @Mock
    PaymentSettingService settingService;
    @Mock
    IPayment payment;

    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    void getAccount() {
        String accountName = "test";
        PaymentSetting.AccountSetting accountSetting = new PaymentSetting.AccountSetting();
        accountSetting.setEnabled(true);
        Account account = new Account();
        account.setName("test");
        account.setChannel("test");
        accountSetting.setAccount(account);

        when(settingService.getAccountSetting(accountName)).thenReturn(Mono.just(accountSetting));

        TestChannelAccount channelAccount = new TestChannelAccount();
        channelAccount.setName("test");
        channelAccount.setChannel("test");
        when(payment.createAccount(any())).thenReturn(Mono.just(channelAccount));
        when(paymentRegistry.get("test")).thenReturn(payment);

        // create account
        Mono<IAccount> accountMono = accountService.getAccount("test");
        accountMono.subscribe(e -> {
            assertSame(e, channelAccount);
        });
        verify(payment, times(1)).createAccount(any());
        verify(paymentRegistry, times(1)).get("test");

        // cache account
        accountMono = accountService.getAccount("test");
        accountMono.subscribe(e -> {
            assertSame(e, channelAccount);
        });
        verify(payment, times(1)).createAccount(any());
        verify(paymentRegistry, times(1)).get("test");
    }
    
}