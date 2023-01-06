package io.mvvm.halo.plugins.payment.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * PaymentDescriptor.
 *
 * @author: pan
 **/
@Data
@Builder
public class PaymentDescriptor {

    @Schema(title = "唯一名称", description = "以英文、横线、下划线组成, 例如：wechat、alipay")
    private String name;

    @Schema(title = "展示名称", description = "例如：微信支付、支付宝")
    private String title;

    @Schema(title = "展示图标", description = "base64格式")
    private String icon;
    
    @Schema(title = "展示Logo", description = "base64格式")
    private String logo;
}
