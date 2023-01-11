package io.mvvm.halo.plugins.payment.sdk.response;

import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * PaymentResponse.
 *
 * @author: pan
 **/
public interface PaymentResponse {

    /**
     * @return 商户订单号
     */
    String getOutTradeNo();

    /**
     * @return 操作是否成功
     */
    boolean isSuccess();

    /**
     * @return 支付状态
     */
    PaymentStatus status();

    /**
     * @return 扩展值
     */
    default Map<String, Object> getExpand() {
        return new HashMap<>();
    }

}
