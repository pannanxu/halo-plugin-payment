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

    @Schema(title = "异常信息")
    private String code;
    @Schema(title = "异常信息")
    private String error;
    @Schema(title = "单号")
    private String outTradeNo;

    public static PaymentResponse error(String code, String error, String outTradeNo) {
        return new ErrorResponse(code, error, outTradeNo);
    }

    public static PaymentResponse error(String code, String error) {
        return new ErrorResponse(code, error, null);
    }

    public static PaymentResponse error(String error) {
        return new ErrorResponse("base_error", error, null);
    }

    public static <T extends ErrorResponse> T error(String error, Class<T> clazz) {
        return error("base_error", error, clazz);
    }

    public static <T extends ErrorResponse> T error(String code, String error, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            instance.setCode(code);
            instance.setError(error);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
