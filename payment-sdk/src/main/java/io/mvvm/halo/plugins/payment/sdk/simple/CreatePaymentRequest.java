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

    @Schema(title = "客户端IP")
    private String clientIp;

    @Schema(title = "设备类型", description = "pc：电脑端网页、mobile：手机端网页、mini：小程序、app：手机app")
    private String device = "pc";

    @Schema(title = "应付总金额")
    private int totalFee;

    @Schema(title = "业务引用标识")
    private String gvk;

    @Schema(title = "扩展值")
    private Map<String, Object> expand;

    @Schema(title = "异步通知地址-域名")
    private String notifyDomain;

}
