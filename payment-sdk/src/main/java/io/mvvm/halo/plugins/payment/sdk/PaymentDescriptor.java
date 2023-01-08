package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import run.halo.app.extension.Unstructured;

/**
 * PaymentDescriptor.
 *
 * @author: pan
 **/
@Data
@Builder
public class PaymentDescriptor {
    /**
     * 规则：x-y
     * <p>
     * x: 支付的唯一ID, 例如：wechat、alipay, 如果多个单词可以通过下划线组成：x_x_x-y
     * <p>
     * y: 支付的场景, 例如app场景就是app，小程序场景就是mini
     * 具体场景可以查看 {@link io.mvvm.halo.plugins.payment.sdk.enums.DeviceType}
     * <p>
     * 微信支付 式例：
     * <p>
     * 默认：wechat
     * <p>
     * app：wechat-app
     * <p>
     * 小程序: wechat-mini
     */
    @Schema(title = "唯一名称", description = "以英文、横线、下划线组成, 例如：wechat、alipay、wechat-app")
    private String name;

    @Schema(title = "展示名称", description = "例如：微信支付、支付宝")
    private String title;

    @Schema(title = "展示图标", description = "base64格式")
    private String icon;

    @Schema(title = "展示Logo", description = "base64格式")
    private String logo;

    @Schema(title = "用户需要输入的数据", description = "所选值会设置在expand参数中")
    private Unstructured userInputFormSchema;
}
