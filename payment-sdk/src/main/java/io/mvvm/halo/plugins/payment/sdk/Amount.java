package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Amount.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
public class Amount {

    @Schema(title = "金额: 分")
    private Integer total;
    @Schema(title = "金额: 元")
    private BigDecimal totalYuan;
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
        if (null == total || total < 0) {
            throw new RuntimeException("金额不能小于0元");
        }
        setTotalYuan(toBigDecimal());
    }

    public static Amount of(Integer total) {
        return new Amount(total);
    }

    public static Amount ofYuan(String total) {
        BigDecimal yuan = new BigDecimal(total).multiply(new BigDecimal("100.00"));
        return new Amount(yuan.intValue());
    }

    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(this.total).multiply(BigDecimal.valueOf(0.01))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String toYuan() {
        return toBigDecimal().toString();
    }
}
