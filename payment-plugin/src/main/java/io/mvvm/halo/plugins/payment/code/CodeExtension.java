package io.mvvm.halo.plugins.payment.code;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import java.util.Date;

/**
 * CodeEx.
 *
 * @author: pan
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@GVK(group = "code", version = "v1alpha1", kind = "Payment", plural = "codes", singular = "code")
public class CodeExtension extends AbstractExtension {

    private Spec spec;

    public enum Status {
        used,
        normal,
        disable
    }

    @Data
    public static class Spec {

        private String outTradeNo;

        private String status;

        private Date usedTime;

    }

}
