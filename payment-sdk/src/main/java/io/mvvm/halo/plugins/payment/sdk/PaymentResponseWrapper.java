package io.mvvm.halo.plugins.payment.sdk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.halo.app.extension.Ref;

/**
 * PaymentResponseWrapper.
 *
 * @author: pan
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseWrapper<T> {

    private T response;

    private Ref type;

}
