package net.nanxu.payment.money;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Money.
 *
 * @author: P
 **/
@Data
@Accessors(chain = true)
public class Money {
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 货币
     */
    private Currency currency;
    /**
     * 汇率
     */
    private ExchangeRate rate;

    public static Money of(BigDecimal amount, Currency base) {
        return new Money().setAmount(amount).setCurrency(base);
    }

    public static Money ofCNY(BigDecimal amount) {
        return of(amount, Currency.CNY);
    }

}
