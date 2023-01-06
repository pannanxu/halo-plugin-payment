package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * PaymentSetting.
 *
 * @author: pan
 **/
@Data
public class PaymentSetting {

    public static final String name = "payment-settings";
    
    public static final String basic = "basic";
    /**
     * 本地文件前缀匹配符
     */
    public static final String LOCAL_FILE_PREFIX = "file://";
    
    @Schema(title = "回调接口的Token")
    private String token;
    
}
