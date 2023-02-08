package io.mvvm.halo.plugins.payment.sdk.response;

import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import io.mvvm.halo.plugins.payment.sdk.exception.ExceptionCode;
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
    private Amount money;
    @Schema(title = "退款金额")
    private Amount refundMoney;
    @Schema(title = "扩展值")
    private Map<String, String> expand;
    @Schema(title = "订单退款请求是否成功")
    private boolean success;
    @Schema(title = "订单状态")
    private PaymentStatus status;

    @Override
    public PaymentStatus status() {
        return status;
    }


    public static RefundPaymentResponse onError(BaseException ex) {
        RefundPaymentResponse response = new RefundPaymentResponse();
        response.setError(ex.getMessage());
        response.setCode(ex.getCode());
        return response;
    }

    public static RefundPaymentResponse onError(String code, String msg) {
        RefundPaymentResponse response = new RefundPaymentResponse();
        response.setError(code);
        response.setCode(msg);
        return response;
    }

    public static RefundPaymentResponse onError(Throwable ex) {
        RefundPaymentResponse response = new RefundPaymentResponse();
        response.setError(ex.getMessage());
        response.setCode(ExceptionCode.error.name());
        return response;
    }
}
