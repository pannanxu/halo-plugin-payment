package net.nanxu.payment.channel.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * PaymentProfile.
 *
 * @author: P
 **/
@Data
@AllArgsConstructor
public class PaymentProfile {

    private final String name;
    private final String displayName;
    private final String icon;

    public static PaymentProfile create(String name, String displayName, String icon) {
        return new PaymentProfile(name, displayName, icon);
    }

}
