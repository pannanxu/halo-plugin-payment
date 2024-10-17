package net.nanxu.payment.model;

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
    CNY("CNY", "人民币", "¥", 2),
    HKD("HKD", "港币", "$", 2),
    USD("USD", "美元", "$", 2),
    EUR("EUR", "欧元", "€", 2),
    GBP("GBP", "英镑", "£", 2),
    JPY("JPY", "日元", "¥", 0),
    ;

    /**
     * ISO 货币代码。
     */
    private final String code;
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
