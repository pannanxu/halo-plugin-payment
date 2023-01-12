package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import java.util.Set;

/**
 * 支付.
 *
 * @author: pan
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@GVK(group = "payment.mvvm.io", version = "v1alpha1", kind = "Payment", plural = "payments", singular = "payment")
public class PaymentExtension extends AbstractExtension {

    public static final String group = "payment.mvvm.io";
    public static final String version = "v1alpha1";
    public static final String kind = "Payment";
    
    private Spec spec;
    
    @Data
    public static class Spec {

        @Schema(title = "展示名称")
        private String displayName;
        @Schema(title = "是否启用")
        private Boolean enabled;

        @Schema(title = "启用的方法", description = "参考IPayment的方法名:create,fetch,cancel,refund")
        private Set<String> enableMethods;
    }

}
