package net.nanxu.payment;

import org.springframework.stereotype.Component;
import run.halo.app.infra.ExternalLinkProcessor;

/**
 * ExternalLinkCreator.
 *
 * @author: P
 **/
@Component
public class ExternalLinkCreator {
    private final ExternalLinkProcessor externalLinkProcessor;

    public ExternalLinkCreator(ExternalLinkProcessor externalLinkProcessor) {
        this.externalLinkProcessor = externalLinkProcessor;
    }

    public String paymentUrl(String orderNo, String channel) {
        return externalLinkProcessor.processLink("/payment/" + orderNo + "/pay/" + channel);
    }

    public String callbackUrl(String internal, String orderNo, String channel) {
        return externalLinkProcessor.processLink("/payment/" + internal + "/" + orderNo + "/callback/" + channel);
    }
}
