package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Schema(title = "黑名单IP")
    private String blackListIp;

    @Schema(title = "限流配置")
    private Limit limit;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(title = "限流配置")
    public static class Limit {
        @Schema(title = "秒")
        private Integer second;
        @Schema(title = "次数")
        private Integer count;

    }

    public Limit getLimit() {
        if (null == this.limit) {
            this.limit = new Limit(5, 3);
        }
        return limit;
    }
}
