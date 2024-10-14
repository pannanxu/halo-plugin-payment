package net.nanxu.payment.infra.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Money.
 *
 * @author: P
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Money {
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 货币
     */
    private Currency currency;

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money ofCNY(BigDecimal amount) {
        return new Money(amount, Currency.CNY);
    }
}
