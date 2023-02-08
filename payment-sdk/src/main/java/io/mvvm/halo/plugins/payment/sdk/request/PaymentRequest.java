package io.mvvm.halo.plugins.payment.sdk.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * PaymentRequest.
 *
 * @author: pan
 **/
public interface PaymentRequest {
    /**
     * @return 商户订单号
     */
    String getOutTradeNo();

    /**
     * @return 扩展值
     */
    default Map<String, String> getExpand() {
        return new HashMap<>();
    }

    @Data
    @Accessors(chain = true)
    class SimplePaymentRequest implements PaymentRequest {
        
        private String outTradeNo;
        private Map<String, String> expand;
    }
    
    default String getExpandAsString(String key) {
        Map<String, String> expand = getExpand();
        if (null != expand) {
            Object val = expand.get(key);
            if (null != val) {
                return val.toString();
            }
        }
        return null;
    }
}
