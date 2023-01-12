package io.mvvm.halo.plugins.payment.sdk;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.function.Predicate;

/**
 * PaymentQuery.
 *
 * @author: pan
 **/
@Data
public class PaymentQuery {
    /**
     * 应用端点，如小程序、公众号、app、pc等
     */
    private String endpoint;
    /**
     * 指定某个支付方式
     */
    private String name;

    public Predicate<IPayment> buildQueryPredicate() {
        return payment -> {
            if (StringUtils.hasLength(this.getEndpoint())
                && !payment.getDescriptor().hasEndpoint(this.getEndpoint())) {
                return Boolean.FALSE;
            }
            if (StringUtils.hasLength(this.getName())
                && !payment.getDescriptor().getName().equals(this.getName())) {
                return Boolean.FALSE;
            }
            return true;
        };
    }
}
