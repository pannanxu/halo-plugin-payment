package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
        this.total = total;
    }

    public Amount(Integer total, String currency) {
        this.total = total;
        this.currency = currency;
    }

    public static Amount of(Integer total) {
        return new Amount(total);
    }
}
