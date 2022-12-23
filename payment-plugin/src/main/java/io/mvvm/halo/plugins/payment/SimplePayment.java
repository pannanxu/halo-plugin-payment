package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import run.halo.app.extension.Ref;

/**
 * SimplePayment.
 *
 * @author: pan
 **/
@Slf4j
public class SimplePayment implements IPayment {

    @Getter
    private IPaymentOperator operator;
    private final Ref type;

    public SimplePayment(IPaymentOperator operator, Ref type) {
        this.operator = operator;
        this.type = type;
    }

    @Override
    public Ref type() {
        return type;
    }

    @Override
    public String toString() {
        return """
                SimplePayment: %s, name: %s
                """.formatted(operator, operator.type().getName());
    }
}
