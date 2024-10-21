package net.nanxu.payment.money;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 货币管理器.
 *
 * @author: P
 **/
public class CurrencyManager {

    private final List<CurrencyUnit> units = new CopyOnWriteArrayList<>();

    public CurrencyManager() {
        units.add(new ISOCurrencyUnit("CNY", 156, "人民币", "¥", 2));
        units.add(new ISOCurrencyUnit("HKD", 344, "港币", "$", 2));
        units.add(new ISOCurrencyUnit("USD", 840, "美元", "$", 2));
        units.add(new ISOCurrencyUnit("EUR", 978, "欧元", "€", 2));
        units.add(new ISOCurrencyUnit("GBP", 826, "英镑", "£", 2));
        units.add(new ISOCurrencyUnit("JPY", 392, "日元", "¥", 0));
    }

}
