package io.mvvm.halo.plugins.payment.sdk.response;

import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * PaymentInfo.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PaymentInfo extends ErrorResponse implements PaymentResponse {

    @Schema(title = "单号")
    private String outTradeNo;
    @Schema(title = "第三方单号")
    private String tradeNo;
    @Schema(title = "应付总金额")
    private Amount money;
    @Schema(title = "实际支付金额")
    private Amount actualMoney;
    @Schema(title = "扩展值")
    private Map<String, Object> expand;
    @Schema(title = "订单是否查询成功")
    private boolean success;
    @Schema(title = "订单状态")
    private PaymentStatus status;
    @Schema(title = "支付成功时间")
    private Date paySuccessTime;
    @Schema(title = "第三方支付成功后返回的参数", description = "需要第三方支付的支持，如果第三方支付不支持的情况下可以考虑使用数据库存储",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String backParams;

    @Override
    public PaymentStatus status() {
        return status;
    }
}
