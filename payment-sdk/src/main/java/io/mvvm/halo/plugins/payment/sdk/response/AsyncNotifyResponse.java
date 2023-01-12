package io.mvvm.halo.plugins.payment.sdk.response;

import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import io.mvvm.halo.plugins.payment.sdk.exception.ExceptionCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.function.Supplier;

/**
 * 异步通知.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AsyncNotifyResponse extends ErrorResponse implements PaymentResponse {

    @Schema(title = "应付总金额")
    private Amount money;
    @Schema(title = "实际支付金额")
    private Amount actualFee;
    @Schema(title = "单号")
    private String outTradeNo;
    @Schema(title = "第三方单号")
    private String tradeNo;
    @Schema(title = "通知是否成功")
    private boolean success;
    @Schema(title = "订单状态")
    private PaymentStatus status;
    @Schema(title = "第三方支付成功后返回的参数", description = "需要第三方支付的支持，如果第三方支付不支持的情况下可以考虑使用数据库存储",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String backParams;

    @Schema(title = "支付模块响应第三方数据-成功")
    private Supplier<Object> responseSuccess;
    @Schema(title = "支付模块响应第三方数据-失败")
    private Supplier<Object> responseFail;

    public static AsyncNotifyResponse onError(BaseException ex) {
        AsyncNotifyResponse response = new AsyncNotifyResponse();
        response.setError(ex.getMessage());
        response.setCode(ex.getCode());
        return response;
    }

    public static AsyncNotifyResponse onError(Throwable ex) {
        AsyncNotifyResponse response = new AsyncNotifyResponse();
        response.setError(ex.getMessage());
        response.setCode(ExceptionCode.error.name());
        return response;
    }

    @Override
    public PaymentStatus status() {
        return status;
    }
}
