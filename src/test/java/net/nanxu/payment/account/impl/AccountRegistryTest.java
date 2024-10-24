package net.nanxu.payment.account.impl;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import net.nanxu.payment.account.TestChannelAccount;
import org.junit.jupiter.api.Test;

class AccountRegistryTest {
    
    AccountRegistry accountRegistry = new AccountRegistry();

    @Test
    void register() {
        TestChannelAccount channelAccount = new TestChannelAccount();
        channelAccount.setName("test");
        accountRegistry.register(channelAccount);

        assertSame(accountRegistry.getAccount("test"), channelAccount);
    }

    @Test
    void unregister() {
        TestChannelAccount channelAccount = new TestChannelAccount();
        channelAccount.setName("test");
        accountRegistry.register(channelAccount);
        accountRegistry.unregister("test");
        assertNull(accountRegistry.getAccount("test"));
    }

}