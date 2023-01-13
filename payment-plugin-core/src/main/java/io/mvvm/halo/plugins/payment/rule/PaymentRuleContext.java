package io.mvvm.halo.plugins.payment.rule;

import lombok.Getter;
import run.halo.app.extension.ReactiveExtensionClient;

import java.util.Objects;

/**
 * PaymentRuleContext.
 *
 * @author: pan
 **/
public class PaymentRuleContext {

    private final ReactiveExtensionClient client;
    @Getter
    private BasePaymentRule first;
    private BasePaymentRule last;

    public PaymentRuleContext(ReactiveExtensionClient client) {
        this.client = client;
    }


    public PaymentRuleContext addHead(BasePaymentRule rule) {
        if (null == rule) {
            return this;
        }

        rule.setClient(this.client);
        
        if (hasHandler(this.first, rule)) {
            return this;
        }
     
        if (null != this.first) {
            rule.setNext(this.first);
            if (null == this.last) {
                this.last = this.first;
            }
        }

        this.first = rule;

        return this;
    }

    public PaymentRuleContext addLast(BasePaymentRule rule) {
        if (null == rule) {
            return this;
        }

        rule.setClient(this.client);

        if (hasHandler(this.first, rule)) {
            return this;
        }
        
        if (null == this.first) {
            return addHead(rule);
        }

        Objects.requireNonNullElseGet(this.last, () -> this.first).setNext(rule);

        this.last = rule;
        this.last.setNext(null);
        return this;
    }

    public boolean hasHandler(BasePaymentRule currentHandler,
                              final BasePaymentRule newHandler) {
        if (null == currentHandler) {
            return false;
        }

        if (currentHandler.equals(newHandler)) {
            return true;
        }

        if (null == currentHandler.getNext()) {
            return false;
        }

        return hasHandler(currentHandler.getNext(), newHandler);
    }
}
