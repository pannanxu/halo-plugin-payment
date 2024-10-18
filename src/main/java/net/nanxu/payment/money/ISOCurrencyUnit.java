package net.nanxu.payment.money;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 国际货币单位.
 *
 * @author: P
 **/
@Getter
@RequiredArgsConstructor
public class ISOCurrencyUnit implements CurrencyUnit {

    /**
     * ISO 货币唯一的三位字母代码。
     */
    private final String alphaCode;
    /**
     * ISO 货币唯一的三位数字代码。
     */
    private final int numericCode;
    /**
     * 货币名称。
     */
    private final String name;
    /**
     * 货币符号。
     */
    private final String symbol;
    /**
     * 小数位数。
     */
    private final int fraction;

}
