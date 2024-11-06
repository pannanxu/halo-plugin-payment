package net.nanxu.payment.money;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 汇率.
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
    private String base;
    /**
     * 目标货币
     */
    private String target;
    /**
     * 汇率
     */
    private BigDecimal rate;
}
