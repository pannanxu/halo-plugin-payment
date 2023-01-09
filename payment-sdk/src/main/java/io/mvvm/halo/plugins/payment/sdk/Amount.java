package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Amount.
 *
 * @author: pan
 **/
@Data
public class Amount {

    @Schema(title = "金额: 分")
    private Integer total;
    @Schema(title = "币种：CNY:人民币")
    private String currency = "CNY";

    public Amount() {
    }

    public Amount(Integer total) {
        this(total, "CNY");
    }

    public Amount(Integer total, String currency) {
        this.total = total;
        this.currency = currency;
        if (total < 0) {
            throw new RuntimeException("金额不能小于0元");
        }
    }

    public static Amount of(Integer total) {
        return new Amount(total);
    }
    
    public BigDecimal toBigDecimal() {
        return new BigDecimal(this.total * 0.01).setScale(2, RoundingMode.HALF_DOWN);
    }
}
