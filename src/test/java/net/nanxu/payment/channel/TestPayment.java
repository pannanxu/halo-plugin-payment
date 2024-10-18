package net.nanxu.payment.channel;

import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.account.TestChannelAccount;
import net.nanxu.payment.channel.model.PaymentProfile;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import reactor.core.publisher.Mono;

/**
 * TestPayment.
 *
 * @author: P
 **/
public class TestPayment implements IPayment {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Mono<IAccount> createAccount(IAccount account) {
        TestChannelAccount channelAccount = new TestChannelAccount();
        channelAccount.copyFrom(account);
        channelAccount.setAppId("test");
        return Mono.just(channelAccount);
    }

    @Override
    public PaymentProfile getProfile() {
        return null;
    }

    @Override
    public IPaymentSupport getSupport() {
        return null;
    }

    @Override
    public IPaymentCallback getCallback() {
        return null;
    }

    @Override
    public Mono<PaymentResult> pay(PaymentRequest request) {
        return null;
    }

    @Override
    public Mono<QueryResult> query(QueryRequest request) {
        return null;
    }

    @Override
    public Mono<RefundResult> refund(RefundRequest request) {
        return null;
    }

    @Override
    public Mono<RefundResult> cancel(RefundRequest request) {
        return null;
    }
}
