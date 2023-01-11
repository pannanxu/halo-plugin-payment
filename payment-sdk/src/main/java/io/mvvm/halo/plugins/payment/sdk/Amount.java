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

    public Amount(BigDecimal totalYuan) {
        if (null == totalYuan || BigDecimal.ZERO.compareTo(totalYuan) > 0) {
            throw new RuntimeException("金额不能小于0元");
        }
        this.totalYuan = totalYuan;
        this.total = totalYuan.multiply(new BigDecimal("100.00"))
                .setScale(2, RoundingMode.HALF_UP)
                .intValue();
    }

    public Amount(String yuan) {
        this(new BigDecimal(yuan));
    }

    public static Amount of(Integer total) {
        return new Amount(total);
    }

    public static Amount of(Integer total, Integer defVal) {
        try {
            return new Amount(total);
        } catch (Exception ex) {
            return new Amount(defVal);
        }
    }

    public static Amount of(String totalStr) {
        return new Amount(Integer.valueOf(totalStr));
    }

    public static Amount of(BigDecimal totalYuan) {
        return new Amount(totalYuan);
    }

    public static Amount ofYuan(String yuanStr, String defaultVal) {
        try {
            return ofYuan(yuanStr);
        } catch (Exception ex) {
            return new Amount(defaultVal);
        }
    }

    public static Amount ofYuan(String yuanStr) {
        BigDecimal yuan = new BigDecimal(yuanStr);
        return new Amount(yuan);
    }

    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(this.total).multiply(new BigDecimal("0.01"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String toYuanStr() {
        return this.totalYuan.toPlainString();
    }
    
    public boolean isFree() {
        return this.total <= 0;
    }
}
