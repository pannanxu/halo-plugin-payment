package io.mvvm.halo.plugins.payment.sdk.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CacheBody.
 *
 * @author: pan
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheBody {

    private Object data;

    private Integer expire;
}
