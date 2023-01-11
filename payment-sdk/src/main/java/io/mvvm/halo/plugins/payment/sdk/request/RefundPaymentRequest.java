package io.mvvm.halo.plugins.payment.sdk.request;

import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * RefundPaymentRequest.
 *
 * @author: pan
 **/
@Data
public class RefundPaymentRequest implements PaymentRequest {

    @Schema(title = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String outTradeNo;
    @Schema(title = "退款单号")
    private String refundNo;
    @Schema(title = "退款金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Amount refundMoney;
    @Schema(title = "退款原因")
    private String refundReason;
    @Schema(title = "退款回调通知地址")
    private String refundNotifyUrl;
    @Schema(title = "扩展值")
    private Map<String, Object> expand;

}
