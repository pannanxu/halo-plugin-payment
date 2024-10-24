package net.nanxu.payment.account;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nanxu.payment.exception.AccountException;

/**
 * 第三方支付账户.
 *
 * @author: P
 **/
public interface IAccount {

    String getName();

    String getChannel();
    
    String getType();

    ObjectNode getConfig();

    @SuppressWarnings("unchecked")
    default <T extends IAccount> T as(Class<T> account) {
        if (!account.isInstance(this)) {
            throw new AccountException("账户类型不匹配");
        }
        return (T) this;
    }

}
