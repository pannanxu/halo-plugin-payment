package net.nanxu.testplugin;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.account.PaymentAccount;
import net.nanxu.payment.infra.AbstractPayment;
import net.nanxu.payment.infra.IPaymentCallback;
import net.nanxu.payment.infra.IPaymentSupport;
import net.nanxu.payment.infra.PaymentProfile;
import net.nanxu.payment.infra.model.CallbackRequest;
import net.nanxu.payment.infra.model.CallbackResult;
import net.nanxu.payment.infra.model.PaymentRequest;
import net.nanxu.payment.infra.model.PaymentResult;
import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.infra.model.QueryRequest;
import net.nanxu.payment.infra.model.QueryResult;
import net.nanxu.payment.infra.model.RefundRequest;
import net.nanxu.payment.infra.model.RefundResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * WeChatPayment.
 *
 * @author: P
 **/
@Slf4j
@Component
public class WeChatPayment extends AbstractPayment {
    public static final String NAME = "WeChat";

    public WeChatPayment() {
        super(NAME,
            PaymentProfile.builder()
                .name(NAME)
                .displayName("微信支付")
                .icon("wechat.png")
                .build(),
            new WeChatPaymentSupport(), new WeChatPaymentCallback());
    }

    @Override
    public Mono<IAccount> createAccount(IAccount account) {
        return Mono.just(new WeChatAccount(account));
    }

    @Override
    public Mono<PaymentResult> pay(PaymentRequest request) {
        log.info("WeChatPayment pay");
        return Mono.just(new PaymentResult());
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

    public static class WeChatPaymentSupport implements IPaymentSupport {
        @Override
        public Mono<Boolean> pay(PaymentSupport request) {
            return Mono.just(request.getOrder().getPayment().getName().equals(NAME));
        }

        @Override
        public Mono<Boolean> query(QueryRequest request) {
            return Mono.just(true);
        }

        @Override
        public Mono<Boolean> refund(RefundRequest request) {
            return Mono.just(true);

        }

        @Override
        public Mono<Boolean> cancel(RefundRequest request) {
            return Mono.just(true);
        }
    }

    public static class WeChatPaymentCallback implements IPaymentCallback {
        @Override
        public Mono<CallbackResult> callback(CallbackRequest request) {
            return null;
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class WeChatAccount extends PaymentAccount {

        private final String secret;
        private final String appId;
        // ...

        public WeChatAccount(IAccount account) {
            super(account);
            this.secret = account.getConfig().get("secret").asText();
            this.appId = account.getConfig().get("appId").asText();
        }

    }

}
