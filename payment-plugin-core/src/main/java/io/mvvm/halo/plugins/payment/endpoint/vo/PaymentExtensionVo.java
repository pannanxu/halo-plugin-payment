package io.mvvm.halo.plugins.payment.endpoint.vo;

import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptorGetter;
import io.mvvm.halo.plugins.payment.sdk.PaymentExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PaymentExtensionVo.
 *
 * @author: pan
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentExtensionVo {
    
    private PaymentExtension extension;
    
    private PaymentDescriptorGetter descriptor;
    
}
