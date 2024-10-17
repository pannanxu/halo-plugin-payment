package net.nanxu.payment.account;

import net.nanxu.payment.channel.IPayment;
import reactor.core.publisher.Mono;

/**
 * AccountService.
 *
 * @author: P
 **/
public interface AccountService {
    /**
     * 获取或者创建一个账户
     *
     * @param name 账户名称
     * @return 由通道创建的账户信息
     */
    Mono<IAccount> getAccount(String name);

    /**
     * 创建一个新的账户
     * @param payment 支付通道
     * @param account 系统设置账户
     * @return 通道账户
     */
    Mono<IAccount> createAccount(IPayment payment, IAccount account);

}
