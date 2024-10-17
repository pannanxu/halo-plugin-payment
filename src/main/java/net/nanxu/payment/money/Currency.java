package net.nanxu.payment.money;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Currency.
 *
 * @author: P
 **/
@Getter
@RequiredArgsConstructor
public enum Currency {
    CNY("CNY", 156,"人民币", "¥", 2),
    HKD("HKD", 344,"港币", "$", 2),
    USD("USD", 840,"美元", "$", 2),
    EUR("EUR", 978,"欧元", "€", 2),
    GBP("GBP", 826,"英镑", "£", 2),
    JPY("JPY",392, "日元", "¥", 0),
    ;

    /**
     * ISO 货币唯一的三位字母代码。
     */
    private final String alphaCode;
    /**
     * ISO 货币唯一的三位数字代码。
     */
    private final Integer numericCode;
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
