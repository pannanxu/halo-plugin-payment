package net.nanxu.payment.account.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.nanxu.payment.account.IAccount;

/**
 * AccountRegistry.
 *
 * @author: P
 **/
public class AccountRegistry {

    private final Map<String, IAccount> accountMap = new ConcurrentHashMap<>();

    public void register(IAccount account) {
        accountMap.put(account.getName(), account);
    }

    public void unregister(String name) {
        accountMap.remove(name);
    }

    public IAccount getAccount(String name) {
        return accountMap.get(name);
    }

    public IAccount getMasterAccount(String channel) {
        return accountMap.values().stream()
            .filter(e -> e.getChannel().equals(channel))
            .filter(IAccount::getMaster)
            .findFirst()
            .orElse(null);
    }
}
