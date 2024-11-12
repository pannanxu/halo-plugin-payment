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
    private String currency;
    /**
     * 汇率
     */
    private ExchangeRate rate;

    public static Money of(Integer amount, String currency) {
        BigDecimal value = new BigDecimal(amount);
        return new Money().setAmount(value.multiply(new BigDecimal("0.01"))).setCurrency(currency);
    }
    
    public static Money of(BigDecimal amount, String currency) {
        return new Money().setAmount(amount).setCurrency(currency);
    }

    public static Money ofCNY(BigDecimal amount) {
        return of(amount, "CNY");
    }

    public static Money ofCNY(String amount) {
        BigDecimal value = new BigDecimal(amount);
        return of(value, "CNY");
    }

    public static Money ofCNY(Long amount) {
        BigDecimal value = new BigDecimal(amount);
        return of(value.multiply(new BigDecimal("0.01")), "CNY");
    }

    public Integer getAmountInCNY() {
        return amount.multiply(new BigDecimal("100.00")).intValue();
    }

}
