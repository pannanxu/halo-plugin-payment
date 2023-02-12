package io.mvvm.halo.plugins.payment.sdk;

import run.halo.app.extension.Unstructured;

import java.util.Set;
import java.util.function.Supplier;

/**
 * PaymentDescriptorGetter.
 *
 * @author: pan
 **/
public interface PaymentDescriptorGetter {

    static PaymentDescriptorGetter of(IPaymentOperator operator, Supplier<Boolean> status) {
        PaymentDescriptor descriptor = operator.getDescriptor();
        return new PaymentDescriptorGetter() {
            @Override
            public String getPluginId() {
                return operator.getPluginWrapper().getPluginId();
            }

            @Override
            public String getName() {
                return descriptor.getName();
            }

            @Override
            public String getTitle() {
                return descriptor.getTitle();
            }

            @Override
            public Unstructured getSchema() {
                return descriptor.getUserInputFormSchema();
            }

            @Override
            public Set<String> getEndpoint() {
                if (null == descriptor.getEndpoint() || descriptor.getEndpoint().isEmpty()) {
                    return Set.of();
                }
                return Set.of(descriptor.getEndpoint().toArray(new String[0]));
            }

            @Override
            public boolean getStatus() {
                return status.get();
            }
        };
    }

    String getPluginId();

    String getName();

    String getTitle();

    Unstructured getSchema();

    default Set<String> getEndpoint() {
        return Set.of();
    }

    boolean getStatus();


}
