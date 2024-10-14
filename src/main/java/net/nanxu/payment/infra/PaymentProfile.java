package net.nanxu.payment.infra;

import lombok.Builder;
import lombok.Data;

/**
 * PaymentProfile.
 *
 * @author: P
 **/
@Data
@Builder
public class PaymentProfile {

    private final String name;
    private final String displayName;
    private final String icon;

}
