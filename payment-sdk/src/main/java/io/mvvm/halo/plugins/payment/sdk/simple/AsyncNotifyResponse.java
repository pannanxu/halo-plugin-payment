package io.mvvm.halo.plugins.payment.sdk.simple;

import io.mvvm.halo.plugins.payment.sdk.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 异步通知.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
public class AsyncNotifyResponse implements PaymentResponse {

    private int totalFee;
    private String outTradeNo;
    private boolean success;
    private PaymentStatus status;
    @Schema(title = "支付模块响应第三方数据")
    private Mono<ServerResponse> response;

    @Override
    public PaymentStatus status() {
        return status;
    }
}
