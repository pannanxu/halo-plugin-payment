package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import run.halo.app.extension.Unstructured;

import java.util.Set;

/**
 * PaymentDescriptor.
 *
 * @author: pan
 **/
@Getter
@Builder
public class PaymentDescriptor {

    @NonNull
    @Schema(title = "唯一名称", description = "以英文、下划线组成, 例如：wechat、alipay、wechat_app")
    private String name;

    @NonNull
    @Schema(title = "展示名称", description = "例如：微信支付、支付宝")
    private String title;

    @Schema(title = "展示图标", description = "base64格式")
    private String icon;

    @Schema(title = "展示Logo", description = "base64格式")
    private String logo;

    @Schema(title = "应用端点, 为空则表示不限制", description = "例如，在PC端网页那就是pc、在移动App那就是app，可以同时支持多个端点")
    private Set<String> endpoint;

    @Schema(title = "用户需要输入的数据", description = "所选值会设置在expand参数中")
    private Unstructured userInputFormSchema;

    public boolean hasEndpoint(String endpoint) {
        return null == this.endpoint || this.endpoint.isEmpty() || this.endpoint.contains(endpoint);
    }
}
