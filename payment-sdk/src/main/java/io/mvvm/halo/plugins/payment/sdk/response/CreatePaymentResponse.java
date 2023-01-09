package io.mvvm.halo.plugins.payment.sdk.response;

import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * CreatePaymentResponse.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CreatePaymentResponse extends ErrorResponse implements PaymentResponse {

    @Schema(title = "单号")
    private String outTradeNo;
    @Schema(title = "第三方单号")
    private String tradeNo;
    @Schema(title = "应付总金额")
    private int totalFee;
    @Schema(title = "扩展值")
    private Map<String, Object> expand;
    @Schema(title = "订单是否创建成功")
    private boolean success;
    @Schema(title = "订单状态")
    private PaymentStatus status;
    /**
     * see: {@link io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode}
     */
    @Schema(title = "订单支付模式(qr:二维码,h5_url:页面跳转;none:无)")
    private String paymentMode;
    @Schema(title = "订单支付模式数据")
    private Object paymentModeData;

    @Override
    public PaymentStatus status() {
        return status;
    }
}
