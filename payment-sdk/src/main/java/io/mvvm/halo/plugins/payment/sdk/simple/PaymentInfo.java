package io.mvvm.halo.plugins.payment.sdk.simple;

import io.mvvm.halo.plugins.payment.sdk.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * PaymentInfo.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
public class PaymentInfo implements PaymentResponse {

    @Schema(title = "单号")
    private String outTradeNo;
    @Schema(title = "应付总金额")
    private Integer totalFee;
    @Schema(title = "实际支付金额")
    private int actualFee;
    @Schema(title = "扩展值")
    private Map<String, Object> expand;
    @Schema(title = "订单是否查询成功")
    private boolean success;
    @Schema(title = "订单状态")
    private PaymentStatus status;

    @Override
    public PaymentStatus status() {
        return status;
    }
}
