package io.mvvm.halo.plugins.payment.sdk.request;

import io.mvvm.halo.plugins.payment.sdk.Amount;
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

    @Schema(title = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String outTradeNo;

    @Schema(title = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(title = "描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(title = "客户端IP", requiredMode = Schema.RequiredMode.REQUIRED)
    private String clientIp;
    /**
     * 设备类型: {@link io.mvvm.halo.plugins.payment.sdk.enums.DeviceType}
     */
    @Schema(title = "设备类型",
            description = "pc：电脑端网页、mobile：手机端网页、mini：小程序、app：手机app",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String device = "pc";

    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Amount money;

    @Schema(title = "业务引用标识", description = "在支付成功后的回调中，会通过此值通过AsyncNotifyManager通知到业务层",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String gvk;

    @Schema(title = "第三方支付成功后返回的参数", description = "需要第三方支付的支持，如果第三方支付不支持的情况下可以考虑使用数据库存储",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String backParams;

    @Schema(title = "扩展值")
    private Map<String, Object> expand;

    @Schema(title = "异步通知地址-域名", description = "开发者无需传递此字段, 内部会自动处理", hidden = true)
    private String notifyUrl;

    public void setNotifyUrl(String notifyUrl) {
        throw new RuntimeException("see: setNotifyUrl(String domain, String paymentType)");
    }

    public void setNotifyUrl(String domain, String paymentType) {
        String domainEnd = domain.endsWith("/") ? "" : "/";
        this.notifyUrl = domain + domainEnd + "apis/plugins.payment/notify/%s/%s/%s"
                .formatted(gvk, outTradeNo, paymentType);
    }
}
