package io.mvvm.halo.plugins.payment.sdk.response;

import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * RefundPaymentResponse.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RefundPaymentResponse extends ErrorResponse implements PaymentResponse {

    @Schema(title = "单号")
    private String outTradeNo;
    @Schema(title = "第三方单号")
    private String tradeNo;
    
    @Schema(title = "退款单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refundNo;
    @Schema(title = "应付总金额")
    private int totalFee;
    @Schema(title = "退款金额")
    private Amount refundAmount;
    @Schema(title = "扩展值")
    private Map<String, Object> expand;
    @Schema(title = "订单是否退款成功")
    private boolean success;
    @Schema(title = "订单状态")
    private PaymentStatus status;

    @Override
    public PaymentStatus status() {
        return status;
    }
}
