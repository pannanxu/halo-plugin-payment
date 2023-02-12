package io.mvvm.halo.plugins.payment.sdk.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * CreatePaymentRequest.
 *
 * @author: pan
 **/
@Data
@Accessors(chain = true)
public class CreatePaymentRequest implements PaymentRequest {

    @Schema(title = "单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String outTradeNo;

    @Schema(title = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(title = "描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Amount money;

    @Schema(title = "创建人", requiredMode = Schema.RequiredMode.REQUIRED)
    private Owner creator;

    @Schema(title = "业务引用标识",
            description = "在支付成功后的回调中，会通过此字段通知到业务层",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Biz biz;

    @Schema(title = "扩展值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> expand;

    @Schema(title = "异步通知地址-域名", description = "开发者无需传递此字段, 内部会自动处理", hidden = true)
    private String notifyUrl;

    public void setNotifyUrl(String notifyUrl) {
        throw new RuntimeException("see: setNotifyUrl(String domain, String paymentType)");
    }

    public void setNotifyUrl(String domain, String token, String paymentType) {
        String domainEnd = domain.endsWith("/") ? "" : "/";
        // /apis/payment/notify/payment/{token}/{gvk}/{name}/{paymentType}
        this.notifyUrl = domain + domainEnd + "apis/payment/notify/%s/%s/%s/%s"
                .formatted(token, biz.getGvk(), outTradeNo, paymentType);
    }

    private String getExpandValue(String key) {
        if (null == expand) {
            expand = new HashMap<>();
        }
        return expand.get(key);
    }

    public CreatePaymentRequest addExpand(String key, String value) {
        if (null == this.expand) {
            this.expand = new HashMap<>();
        }
        this.expand.put(key, value);
        return this;
    }

    @Data
    @Accessors(chain = true)
    public static class Owner {
        @Schema(title = "UA", requiredMode = Schema.RequiredMode.REQUIRED)
        private String userAgent;

        @Schema(title = "客户端IP", requiredMode = Schema.RequiredMode.REQUIRED)
        private String ipAddress;

        /**
         * 设备类型: {@link io.mvvm.halo.plugins.payment.sdk.enums.DeviceType}
         */
        @Schema(title = "设备类型", requiredMode = Schema.RequiredMode.REQUIRED)
        private String device = "pc";

        @Schema(title = "下单用户")
        private OwnerRef owner;
    }

    @Data
    @Accessors(chain = true)
    public static class OwnerRef {
        public static final String AVATAR_ANNO = "avatar";
        public static final String WEBSITE_ANNO = "website";

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        private String kind;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 64)
        private String name;

        private String displayName;

        private Map<String, String> annotations;

        @Nullable
        @JsonIgnore
        public String getAnnotation(String key) {
            return annotations == null ? null : annotations.get(key);
        }

        public enum Types {
            WeChatOpenId,
            AliPayOpenId,
            Email
        }
    }

    @Data
    @Accessors(chain = true)
    public static class Biz {

        @Schema(title = "业务引用标识", description = "在支付成功后的回调中，会通过此字段通知到业务层",
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String gvk;

        @Schema(title = "第三方支付成功后返回的参数", description = "需要第三方支付的支持，如果第三方支付不支持的情况下可以考虑使用数据库存储")
        private String backParams;

    }
}
