package io.mvvm.halo.plugins.payment.sdk.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * FetchRefundPaymentRequest.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
public class FetchRefundPaymentRequest implements PaymentRequest {

    @Schema(title = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String outTradeNo;
    @Schema(title = "退款单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refundNo;
    @Schema(title = "扩展值")
    private Map<String, String> expand;

}
