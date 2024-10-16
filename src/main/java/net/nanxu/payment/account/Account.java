package net.nanxu.payment.account;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;

/**
 * Account.
 *
 * @author: P
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class Account extends AbstractExtension implements IAccount  {

    // 账户名称
    private String name;

    // 支付通道
    private String channel;

    // 是否为默认
    private Boolean master;

    // 账户配置信息
    private ObjectNode config;
}
