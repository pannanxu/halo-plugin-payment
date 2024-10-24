package net.nanxu.payment.account;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * PaymentAccount.
 *
 * @author: P
 **/
public abstract class PaymentAccount implements IAccount {
    private final IAccount account;

    public PaymentAccount(IAccount account) {
        this.account = account;
    }

    @Override
    public String getName() {
        return account.getName();
    }

    @Override
    public String getChannel() {
        return account.getChannel();
    }

    @Override
    public ObjectNode getConfig() {
        return account.getConfig();
    }

    @Override
    public String getType() {
        return account.getType();
    }
}
