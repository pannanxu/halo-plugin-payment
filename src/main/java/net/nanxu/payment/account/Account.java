package net.nanxu.payment.account;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import run.halo.app.extension.AbstractExtension;

/**
 * Account.
 *
 * @author: P
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Account extends AbstractExtension implements IAccount {

    // 账户名称
    private String name;

    // 支付通道
    private String channel;

    /**
     * 账户类型
     */
    private String type;

    // 是否为默认
    private Boolean master;

    // 账户配置信息
    private ObjectNode config;

    public void copyFrom(IAccount account) {
        this.setName(account.getName());
        this.setChannel(account.getChannel());
        this.setMaster(account.getMaster());
        this.setConfig(account.getConfig());
    }
}
