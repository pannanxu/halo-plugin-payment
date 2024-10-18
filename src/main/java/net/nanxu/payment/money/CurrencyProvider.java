package net.nanxu.payment.money;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 货币提供商.
 *
 * @author: P
 **/
public class CurrencyProvider {

    private final List<CurrencyUnit> units = new CopyOnWriteArrayList<>();

    public CurrencyProvider() {
        units.add(new ISOCurrencyUnit("CNY", 156, "人民币", "¥", 2));
        units.add(new ISOCurrencyUnit("HKD", 344, "港币", "$", 2));
        units.add(new ISOCurrencyUnit("USD", 840, "美元", "$", 2));
        units.add(new ISOCurrencyUnit("EUR", 978, "欧元", "€", 2));
        units.add(new ISOCurrencyUnit("GBP", 826, "英镑", "£", 2));
        units.add(new ISOCurrencyUnit("JPY", 392, "日元", "¥", 0));
    }

}
