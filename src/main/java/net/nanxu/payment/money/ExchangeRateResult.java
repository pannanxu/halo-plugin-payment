package net.nanxu.payment.money;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

/**
 * ExchangeRateResult.
 *
 * @author: P
 **/
@Data
public class ExchangeRateResult {

    private String base;
    private Long timestamp;
    private Map<String, BigDecimal> rates;
}
