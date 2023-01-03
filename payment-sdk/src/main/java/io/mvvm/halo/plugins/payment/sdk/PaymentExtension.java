package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import static io.mvvm.halo.plugins.payment.sdk.PaymentExtension.group;
import static io.mvvm.halo.plugins.payment.sdk.PaymentExtension.kind;
import static io.mvvm.halo.plugins.payment.sdk.PaymentExtension.version;

/**
 * 支付.
 *
 * @author: pan
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@GVK(group = group, version = version, kind = kind, plural = "payments", singular = "payment")
public class PaymentExtension extends AbstractExtension {

    public static final String group = "payment";
    public static final String version = "v1alpha1";
    public static final String kind = "Payment";

    @Schema(title = "展示名称")
    private String displayName;
}
