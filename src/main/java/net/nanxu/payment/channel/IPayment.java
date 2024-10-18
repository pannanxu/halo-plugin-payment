package net.nanxu.payment.channel;

import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.channel.model.PaymentProfile;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import org.pf4j.ExtensionPoint;
import reactor.core.publisher.Mono;

/**
 * IPayment.
 *
 * @author: P
 **/
public interface IPayment extends ExtensionPoint {

    String getName();

    /**
     * 创建通道的账户。
     *
     * @param account 账户的配置信息
     * @return 通道所需要的账户信息，可以是证书、密钥等，在后续的方法request参数中会将此账户信息传入。
     */
    Mono<IAccount> createAccount(IAccount account);

    PaymentProfile getProfile();

    IPaymentSupport getSupport();

    IPaymentCallback getCallback();

    Mono<PaymentResult> pay(PaymentRequest request);

    Mono<QueryResult> query(QueryRequest request);

    Mono<RefundResult> refund(RefundRequest request);

    Mono<RefundResult> cancel(RefundRequest request);

    /**
     * 注册时调用
     */
    default void register() {
    }

    /**
     * 注销时调用
     */
    default void unregister() {
    }

}
