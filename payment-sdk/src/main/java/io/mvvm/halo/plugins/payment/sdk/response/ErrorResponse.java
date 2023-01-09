package io.mvvm.halo.plugins.payment.sdk.response;

import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * ErrorResponse.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse implements PaymentResponse {

    @Schema(title = "单号")
    private String outTradeNo;
    @Schema(title = "异常信息")
    private String error;

    public static PaymentResponse error(String error, String outTradeNo) {
        return new ErrorResponse(error, outTradeNo);
    }

    public static PaymentResponse error(String error) {
        return new ErrorResponse(error, null);
    }

    @Override
    public int getTotalFee() {
        return 0;
    }

    @Override
    public String getTradeNo() {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public PaymentStatus status() {
        return null;
    }
}
