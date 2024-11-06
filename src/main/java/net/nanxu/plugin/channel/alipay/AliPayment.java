package net.nanxu.plugin.channel.alipay;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.account.PaymentAccount;
import net.nanxu.payment.channel.AbstractPayment;
import net.nanxu.payment.channel.IPaymentCallback;
import net.nanxu.payment.channel.IPaymentSupport;
import net.nanxu.payment.channel.model.CallbackRequest;
import net.nanxu.payment.channel.model.CallbackResult;
import net.nanxu.payment.channel.model.PaymentProfile;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import net.nanxu.payment.channel.model.SettingField;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * AliPayment.
 *
 * @author: P
 **/
@Component
public class AliPayment extends AbstractPayment {

    public static final String NAME = "Ali";

    public AliPayment() {
        super(PaymentProfile.create(NAME, "支付宝", "/ali.png"),
            List.of(SettingField.text("appid", "APPID").required(),
                SettingField.text("secret", "SECRET").required(),
                SettingField.text("mchid", "MCHID").required()),
            new AliPaymentSupport(),
            new AliPaymentCallback());
    }

    @Override
    public Mono<IAccount> createAccount(IAccount account) {
        return Mono.just(new AliAccount(account));
    }

    @Override
    public Mono<PaymentResult> pay(PaymentRequest request) {
        AliAccount account = request.getAccount().as(AliAccount.class);
        System.out.println(account);
        return Mono.just(PaymentResult.builder().build());
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


    public static class AliPaymentSupport implements IPaymentSupport {
 
    }

    public static class AliPaymentCallback implements IPaymentCallback {
        @Override
        public Mono<CallbackResult> callback(CallbackRequest request) {
            return null;
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class AliAccount extends PaymentAccount {

        private final String key;
        private final String secret;
        private final String appId;
        // ...

        public AliAccount(IAccount account) {
            super(account);
            this.key = account.getConfig().get("key").asText();
            this.secret = account.getConfig().get("secret").asText();
            this.appId = account.getConfig().get("appId").asText();
        }

    }

}
