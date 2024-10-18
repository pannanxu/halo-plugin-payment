package net.nanxu.payment.acl;

import net.nanxu.payment.generator.PaymentLinkGenerator;
import org.springframework.stereotype.Component;
import run.halo.app.infra.ExternalLinkProcessor;

/**
 * ExternalLinkCreator.
 *
 * @author: P
 **/
@Component
public class PaymentLinkCreator implements PaymentLinkGenerator {
    private final ExternalLinkProcessor externalLinkProcessor;

    public PaymentLinkCreator(ExternalLinkProcessor externalLinkProcessor) {
        this.externalLinkProcessor = externalLinkProcessor;
    }

    @Override
    public String checkoutUrl(String orderNo, String channel) {
        return externalLinkProcessor.processLink("/payment/" + orderNo + "/pay/" + channel);
    }

    @Override
    public String callbackUrl(String internal, String orderNo, String channel) {
        return externalLinkProcessor.processLink("/payment/" + internal + "/" + orderNo + "/callback/" + channel);
    }
}
