package io.mvvm.halo.plugins.payment.sdk.simple;

import io.mvvm.halo.plugins.payment.sdk.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 异步通知.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
public class AsyncNotifyResponse implements PaymentResponse {

    private int totalFee;
    private String outTradeNo;
    private String tradeNo;
    @Schema(title = "通知是否成功")
    private boolean success;
    private PaymentStatus status;
    @Schema(title = "业务模块")
    private String gvk;
    @Schema(title = "支付模块响应第三方数据")
    private Object response;

    @Override
    public PaymentStatus status() {
        return status;
    }
}
