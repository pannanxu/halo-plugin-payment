package net.nanxu.payment.service;

import net.nanxu.payment.account.IAccount;
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

}
