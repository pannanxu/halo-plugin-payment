package io.mvvm.halo.plugins.payment.sdk.response;

import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * CancelPaymentResponse.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
public class CancelPaymentResponse implements PaymentResponse {

    @Schema(title = "单号")
    private String outTradeNo;
    @Schema(title = "第三方单号")
    private String tradeNo;
    @Schema(title = "应付总金额")
    private Amount money;
    @Schema(title = "扩展值")
    private Map<String, String> expand;
    @Schema(title = "订单是否取消成功")
    private boolean success;
    @Schema(title = "订单状态")
    private PaymentStatus status;

    @Override
    public PaymentStatus status() {
        return status;
    }
}
