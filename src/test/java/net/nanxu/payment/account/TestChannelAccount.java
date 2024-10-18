package net.nanxu.payment.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TestChannelAccount.
 *
 * @author: P
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class TestChannelAccount extends Account {
    
    private String appId;

    
}
