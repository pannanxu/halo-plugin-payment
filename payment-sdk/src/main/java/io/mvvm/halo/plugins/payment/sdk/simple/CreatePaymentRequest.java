package io.mvvm.halo.plugins.payment.sdk.simple;

import io.mvvm.halo.plugins.payment.sdk.PaymentRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * CreatePaymentRequest.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
public class CreatePaymentRequest implements PaymentRequest {

    @Schema(title = "单号")
    private String outTradeNo;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "创建人IP")
    private String createIp;

    @Schema(title = "应付总金额")
    private Integer totalFee;

    @Schema(title = "扩展值")
    private Map<String, Object> expand;

    @Schema(title = "异步通知地址")
    private String notifyUrl;

}
