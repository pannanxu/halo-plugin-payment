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
    @Schema(title = "退款单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refundNo;
    @Schema(title = "订单金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Amount money;
    @Schema(title = "退款金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Amount refundMoney;
    @Schema(title = "退款原因")
    private String refundReason;
    @Schema(title = "业务引用标识", description = "在退款成功后的回调中，会通过此字段通知到业务层",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String gvk;
    @Schema(title = "扩展值")
    private Map<String, String> expand;

    @Schema(title = "退款回调通知地址", hidden = true, description = "开发者无需设置, 内部会自动处理")
    private String refundNotifyUrl;

    public void setRefundNotifyUrl(String refundNotifyUrl) {
        throw new RuntimeException("use setRefundNotifyUrl(String refundNotifyUrl, String paymentType)");
    }

    public void setRefundNotifyUrl(String domain, String token, String paymentType) {
        String domainEnd = domain.endsWith("/") ? "" : "/";
        // /apis/payment/notify/refund/{token}/{gvk}/{name}/{refundNo}/{paymentType}
        this.refundNotifyUrl = domain + domainEnd + "apis/payment/notify/refund/%s/%s/%s/%s/%s"
                .formatted(token, gvk, outTradeNo, refundNo, paymentType);
    }


}
