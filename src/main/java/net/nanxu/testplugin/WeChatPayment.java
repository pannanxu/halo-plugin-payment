package net.nanxu.testplugin;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.account.PaymentAccount;
import net.nanxu.payment.channel.AbstractPayment;
import net.nanxu.payment.channel.IPaymentCallback;
import net.nanxu.payment.channel.IPaymentSupport;
import net.nanxu.payment.channel.PaymentProfile;
import net.nanxu.payment.channel.SettingField;
import net.nanxu.payment.channel.model.CallbackRequest;
import net.nanxu.payment.channel.model.CallbackResult;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.PaymentSupport;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import net.nanxu.payment.reconciliation.IReconciliation;
import net.nanxu.payment.reconciliation.ReconciliationOrder;
import net.nanxu.payment.reconciliation.ReconciliationRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
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
        super(PaymentProfile.create(NAME, "微信支付", "/wechat.png"),
            List.of(SettingField.text("appid", "APPID").required(),
                SettingField.text("secret", "SECRET").required(),
                SettingField.text("mchid", "MCHID").required()),
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

    @Component
    public static class WeChatReconciliation implements IReconciliation {
        @Override
        public Flux<ReconciliationOrder> reconciliation(ReconciliationRequest request) {
            return Flux.empty();
        }
    }

    public static class WeChatPaymentSupport implements IPaymentSupport {
        @Override
        public Mono<Boolean> pay(PaymentSupport request) {
            return Mono.just(request.getOrder().getChannel().getName().equals(NAME));
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
