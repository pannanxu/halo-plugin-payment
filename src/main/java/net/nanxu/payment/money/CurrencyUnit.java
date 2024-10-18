package net.nanxu.payment.money;

/**
 * CurrencyUnit.
 *
 * @author: pan
 **/
public interface CurrencyUnit {

    /**
     * ISO 货币唯一的三位字母代码。
     */
    String getAlphaCode();

    /**
     * ISO 货币唯一的三位数字代码。
     */
    int getNumericCode();

    /**
     * 货币名称。
     */
    String getName();

    /**
     * 货币符号。
     */
    String getSymbol();

    /**
     * 小数位数。
     */
    int getFraction();
}
