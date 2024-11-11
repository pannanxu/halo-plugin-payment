package net.nanxu.plugin.channel;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.http.AbstractHttpClient;
import com.wechat.pay.java.core.http.DefaultHttpClientBuilder;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import net.nanxu.payment.exception.CallbackException;
import net.nanxu.payment.exception.PaymentException;
import net.nanxu.payment.money.Money;
import net.nanxu.payment.order.Order;
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

    public static final String NATIVE_METHOD = "NATIVE";
    public static final String JSAPI_METHOD = "JSAPI";
    public static final String APP_METHOD = "APP";
    public static final String H5_METHOD = "H5";

    public WeChatPayment() {
        super(PaymentProfile.create(NAME, "微信支付", "/wechat.png"),
            List.of(SettingField.text("appId", "appId").required(),
                SettingField.text("merchantId", "商户号").required(),
                SettingField.password("privateKeyPath", "商户API私钥路径").required(),
                SettingField.password("merchantSerialNumber", "商户证书序列号").required(),
                SettingField.password("apiV3Key", "商户APIV3密钥").required()
            ),
            new WeChatPaymentSupport(), new WeChatPaymentCallback());
    }

    @Override
    public Mono<IAccount> createAccount(IAccount account) {
        return Mono.just(new WeChatAccount(account));
    }

    @Override
    public Mono<PaymentResult> pay(PaymentRequest request) {
        return Mono.defer(() -> {
            if (NATIVE_METHOD.equals(request.getOrder().getChannel().getMethod())) {
                try {
                    var prepayRequest = new PrepayRequest();
                    Amount amount = new Amount();
                    amount.setTotal(request.getOrder().getMoney().getAmountInCNY());
                    prepayRequest.setAmount(amount);
                    prepayRequest.setAppid(request.getAccount().as(WeChatAccount.class).getAppId());
                    prepayRequest.setMchid(
                        request.getAccount().as(WeChatAccount.class).getMerchantId());
                    prepayRequest.setDescription(request.getOrder().getSubject());
                    prepayRequest.setNotifyUrl(request.getOrder().getChannel().getNotifyUrl());
                    prepayRequest.setOutTradeNo(request.getOrder().getOrderNo());

                    PrepayResponse response = request.getAccount().as(WeChatAccount.class)
                        .getNativePayService().prepay(prepayRequest);

                    return Mono.just(PaymentResult.builder().order(request.getOrder())
                        .type(PaymentResult.Type.QRCode)
                        .content(response.getCodeUrl())
                        .status(PaymentResult.Status.SUCCESS)
                        .expiresAt(Instant.now().plusSeconds(600))
                        .build());
                } catch (Exception ex) {
                    log.error("WeChatPayment|创建Native支付异常|{}", ex.getMessage());
                    return Mono.error(new PaymentException(ex));
                }
            }
            if (JSAPI_METHOD.equals(request.getOrder().getChannel().getMethod())) {
                try {
                    var prepayRequest =
                        new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
                    var amount = new com.wechat.pay.java.service.payments.jsapi.model.Amount();
                    amount.setTotal(request.getOrder().getMoney().getAmountInCNY());
                    prepayRequest.setAmount(amount);
                    prepayRequest.setAppid(request.getAccount().as(WeChatAccount.class).getAppId());
                    prepayRequest.setMchid(
                        request.getAccount().as(WeChatAccount.class).getMerchantId());
                    prepayRequest.setDescription(request.getOrder().getSubject());
                    prepayRequest.setNotifyUrl(request.getOrder().getChannel().getNotifyUrl());
                    prepayRequest.setOutTradeNo(request.getOrder().getOrderNo());

                    var response = request.getAccount().as(WeChatAccount.class)
                        .getJsapiService().prepay(prepayRequest);
                    return Mono.just(PaymentResult.builder().order(request.getOrder())
                        .type(PaymentResult.Type.Transmission)
                        .content(response.getPrepayId())
                        .status(PaymentResult.Status.SUCCESS)
                        .expiresAt(Instant.now().plusSeconds(600))
                        .build());
                } catch (Exception ex) {
                    log.error("WeChatPayment|创建JSAPI支付异常|{}", ex.getMessage());
                    return Mono.error(new PaymentException(ex));
                }
            }
            if (APP_METHOD.equals(request.getOrder().getChannel().getMethod())) {
                try {
                    var prepayRequest =
                        new com.wechat.pay.java.service.payments.app.model.PrepayRequest();
                    var amount = new com.wechat.pay.java.service.payments.app.model.Amount();
                    amount.setTotal(request.getOrder().getMoney().getAmountInCNY());
                    prepayRequest.setAmount(amount);
                    prepayRequest.setAppid(request.getAccount().as(WeChatAccount.class).getAppId());
                    prepayRequest.setMchid(
                        request.getAccount().as(WeChatAccount.class).getMerchantId());
                    prepayRequest.setDescription(request.getOrder().getSubject());
                    prepayRequest.setNotifyUrl(request.getOrder().getChannel().getNotifyUrl());
                    prepayRequest.setOutTradeNo(request.getOrder().getOrderNo());

                    var response = request.getAccount().as(WeChatAccount.class)
                        .getAppService().prepay(prepayRequest);
                    return Mono.just(PaymentResult.builder().order(request.getOrder())
                        .type(PaymentResult.Type.Transmission)
                        .content(response.getPrepayId())
                        .status(PaymentResult.Status.SUCCESS)
                        .expiresAt(Instant.now().plusSeconds(600))
                        .build());
                } catch (Exception ex) {
                    log.error("WeChatPayment|创建APP支付异常|{}", ex.getMessage());
                    return Mono.error(new PaymentException(ex));
                }
            }
            if (H5_METHOD.equals(request.getOrder().getChannel().getMethod())) {
                try {
                    var prepayRequest =
                        new com.wechat.pay.java.service.payments.h5.model.PrepayRequest();
                    var amount = new com.wechat.pay.java.service.payments.h5.model.Amount();
                    amount.setTotal(request.getOrder().getMoney().getAmountInCNY());
                    prepayRequest.setAmount(amount);
                    prepayRequest.setAppid(request.getAccount().as(WeChatAccount.class).getAppId());
                    prepayRequest.setMchid(
                        request.getAccount().as(WeChatAccount.class).getMerchantId());
                    prepayRequest.setDescription(request.getOrder().getSubject());
                    prepayRequest.setNotifyUrl(request.getOrder().getChannel().getNotifyUrl());
                    prepayRequest.setOutTradeNo(request.getOrder().getOrderNo());

                    var response = request.getAccount().as(WeChatAccount.class)
                        .getH5Service().prepay(prepayRequest);
                    return Mono.just(PaymentResult.builder().order(request.getOrder())
                        .type(PaymentResult.Type.Redirect)
                        .content(response.getH5Url())
                        .status(PaymentResult.Status.SUCCESS)
                        .expiresAt(Instant.now().plusSeconds(600))
                        .build());
                } catch (Exception ex) {
                    log.error("WeChatPayment|创建H5支付异常|{}", ex.getMessage());
                    return Mono.error(new PaymentException(ex));
                }
            }

            log.error("WeChatPayment|创建支付|不受支持的支付方式");
            return Mono.error(new PaymentException("WeChat不受支持的支付方式"));
        });
    }

    @Override
    public Mono<QueryResult> query(QueryRequest request) {
        return Mono.defer(() -> {
            var account = request.getAccount().as(WeChatAccount.class);
            var queryRequest = new QueryOrderByOutTradeNoRequest();
            queryRequest.setMchid(account.getMerchantId());
            queryRequest.setOutTradeNo(request.getOrderNo());

            var transaction =
                account.getNativePayService().queryOrderByOutTradeNo(queryRequest);

            var builder = QueryResult.builder()
                .orderNo(transaction.getOutTradeNo())
                .outTradeNo(transaction.getTransactionId())
                .method(transaction.getTradeType().name())
                .money(Money.of(transaction.getAmount().getPayerTotal(),
                    transaction.getAmount().getPayerCurrency()));

            if (transaction.getTradeState().equals(Transaction.TradeStateEnum.SUCCESS)) {
                return Mono.just(builder.payStatus(Order.OrderStatus.SUCCESS).build());
            }
            if (transaction.getTradeState().equals(Transaction.TradeStateEnum.NOTPAY) ||
                transaction.getTradeState().equals(Transaction.TradeStateEnum.USERPAYING)
            ) {
                return Mono.just(builder.payStatus(Order.OrderStatus.WAITING).build());
            }
            return Mono.just(builder.payStatus(Order.OrderStatus.CLOSED).build());
        });
    }

    @Override
    public Mono<RefundResult> refund(RefundRequest request) {
        return Mono.empty();
    }

    @Override
    public Mono<RefundResult> cancel(RefundRequest request) {
        return Mono.defer(() -> {
            try {
                var account = request.getAccount().as(WeChatAccount.class);
                var close = new CloseOrderRequest();
                close.setMchid(account.getMerchantId());
                close.setOutTradeNo(request.getOrderNo());
                account.getNativePayService().closeOrder(close);
                return Mono.just(RefundResult.builder()
                    .orderNo(request.getOrderNo())
                    .outTradeNo(request.getOutTradeNo())
                    .success(true)
                    .build());
            } catch (Exception ex) {
                log.error("WeChatPayment|关闭订单异常|{}", ex.getMessage());
                return Mono.error(new PaymentException("WeChat订单关闭失败"));
            }
        });
    }

    @Component
    public static class WeChatReconciliation implements IReconciliation {
        @Override
        public Flux<ReconciliationOrder> reconciliation(ReconciliationRequest request) {
            return Flux.empty();
        }
    }

    public static class WeChatPaymentSupport implements IPaymentSupport {

    }

    public static class WeChatPaymentCallback implements IPaymentCallback {
        @Override
        public Mono<CallbackResult> callback(CallbackRequest request) {
            return Mono.defer(() -> {
                var account = request.getAccount().as(WeChatAccount.class);

                try {
                    RequestParam requestParam = new RequestParam.Builder()
                        .serialNumber(request.getPacket().getHeaders().get("Wechatpay-Serial"))
                        .nonce(request.getPacket().getHeaders().get("Wechatpay-Nonce"))
                        .signature(request.getPacket().getHeaders().get("Wechatpay-Signature"))
                        .timestamp(request.getPacket().getHeaders().get("Wechatpay-Timestamp"))
                        .body(request.getPacket().getBody())
                        .build();

                    // 以支付通知回调为例，验签、解密并转换成 Transaction
                    Transaction transaction = account.getParser().parse(requestParam, Transaction.class);
                    return Mono.just(CallbackResult.builder().success(true).render("success").build());
                } catch (ValidationException ex) {
                    // 签名验证失败，返回 401 UNAUTHORIZED 状态码
                    log.error("WeChatPayment|签名验证失败|{}", ex.getMessage());
                    return Mono.error(new CallbackException("微信支付回调签名验证失败"));
                }
            });
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class WeChatAccount extends PaymentAccount {


        private final NativePayService nativePayService;
        private final JsapiService jsapiService;
        private final AppService appService;
        private final H5Service h5Service;

        private final NotificationParser parser;

        private final String appId;
        private final String merchantId;

        public WeChatAccount(IAccount account) {
            super(account);
            this.appId = account.getConfigText("appId");
            this.merchantId = account.getConfigText("merchantId");
            Config wechatConfig = new RSAAutoCertificateConfig.Builder()
                /* 商户号 */
                .merchantId(account.getConfigText("merchantId"))
                /* 商户API私钥路径 */
                .privateKeyFromPath(account.getConfigText("privateKeyPath"))
                /* 商户证书序列号 */
                .merchantSerialNumber(account.getConfigText("merchantSerialNumber"))
                /* 商户APIV3密钥 */
                .apiV3Key(account.getConfigText("apiV3Key"))
                .build();
            NotificationConfig notificationConfig = new RSAAutoCertificateConfig.Builder()
                .merchantId(merchantId)
                .privateKeyFromPath(account.getConfigText("privateKeyPath"))
                .merchantSerialNumber(account.getConfigText("merchantSerialNumber"))
                .apiV3Key(account.getConfigText("apiV3Key"))
                .build();
            AbstractHttpClient client = new DefaultHttpClientBuilder().config(wechatConfig).build();
            this.nativePayService = new NativePayService.Builder().httpClient(client).build();
            this.jsapiService = new JsapiService.Builder().httpClient(client).build();
            this.appService = new AppService.Builder().httpClient(client).build();
            this.h5Service = new H5Service.Builder().httpClient(client).build();

            this.parser = new NotificationParser(notificationConfig);
        }
    }

}
