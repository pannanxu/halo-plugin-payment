package net.nanxu.payment.money;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.math.BigDecimal;

/**
 * ExchangeRate.
 *
 * @author: P
 **/
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class ExchangeRate {
    /**
     * 基础货币
     */
    private Currency base;
    /**
     * 目标货币
     */
    private Currency target;
    /**
     * 汇率
     */
    private BigDecimal rate;
}
