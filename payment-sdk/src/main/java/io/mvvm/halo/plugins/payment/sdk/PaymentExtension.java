package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * 支付.
 *
 * @author: pan
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@GVK(group = "payment", version = "v1alpha1", kind = "Payment", plural = "payments", singular = "payment")
public class PaymentExtension extends AbstractExtension {

    @Schema(title = "展示名称")
    private String displayName;
}
