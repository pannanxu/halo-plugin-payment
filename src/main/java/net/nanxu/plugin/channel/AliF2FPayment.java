package net.nanxu.plugin.channel;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
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
import net.nanxu.payment.channel.model.PaymentSupport;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import net.nanxu.payment.channel.model.SettingField;
import net.nanxu.payment.exception.PaymentException;
import net.nanxu.payment.utils.JsonNodeUtils;
import reactor.core.publisher.Mono;
import run.halo.app.infra.utils.JsonUtils;

/**
 * 支付宝当面付.
 *
 * @author: P
 **/
@Slf4j
public class AliF2FPayment extends AbstractPayment {
    public static final String NAME = "AliF2F";

    public AliF2FPayment() {
        super(PaymentProfile.create(NAME, "支付宝当面付", "/ali.png"),
            List.of(SettingField.text("appid", "APPID").required(),
                SettingField.text("secret", "SECRET").required(),
                SettingField.text("mchid", "MCHID").required()),
            new AliF2FPaymentSupport(),
            new AliF2FPaymentCallback());
    }

    @Override
    public Mono<IAccount> createAccount(IAccount account) {
        return Mono.defer(() -> Mono.just(new AliF2FAccount(account)));
    }

    @Override
    public Mono<PaymentResult> pay(PaymentRequest request) {
        return Mono.defer(() -> {
            AlipayTradePrecreateRequest precreateRequest = new AlipayTradePrecreateRequest();
            precreateRequest.setBizContent(JsonUtils.objectToJson(Map.of(
                "out_trade_no", request.getOrder().getOrderNo(),
                "total_amount", request.getOrder().getMoney().getAmount(),
                "subject", request.getOrder().getSubject(),
                "store_id", "JM01",
                "qr_code_timeout_express", "30m"
            )));
            precreateRequest.setNotifyUrl(request.getOrder().getChannel().getNotifyUrl());
            AlipayTradePrecreateResponse response;
            try {
                response = request.getAccount().as(AliF2FAccount.class)
                    .getClient().execute(precreateRequest);
                if (!response.isSuccess()) {
                    log.debug("Payment|支付宝当面付|下单失败:{}", response.getMsg());
                    return Mono.error(new PaymentException("创建当面付订单失败"));
                }
            } catch (AlipayApiException e) {
                log.error("Payment|支付宝当面付|下单异常:{}", e.getMessage(), e);
                return Mono.error(new PaymentException("创建当面付订单异常", e));
            }

            return Mono.just(PaymentResult.builder()
                .status(PaymentResult.Status.SUCCESS)
                .type(PaymentResult.Type.QRCode)
                .content(response.getQrCode())
                .build());
        });
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

    public static class AliF2FPaymentSupport implements IPaymentSupport {
        @Override
        public Mono<Boolean> pay(PaymentSupport request) {
            return Mono.just(Boolean.TRUE);
        }

        @Override
        public Mono<Boolean> query(QueryRequest request) {
            return Mono.just(Boolean.TRUE);
        }

        @Override
        public Mono<Boolean> refund(RefundRequest request) {
            return Mono.just(Boolean.TRUE);

        }

        @Override
        public Mono<Boolean> cancel(RefundRequest request) {
            return Mono.just(Boolean.TRUE);
        }
    }

    @Slf4j
    public static class AliF2FPaymentCallback implements IPaymentCallback {
        @Override
        public Mono<CallbackResult> callback(CallbackRequest request) {
            return Mono.defer(() -> doCallback(request));
        }

        private Mono<CallbackResult> doCallback(CallbackRequest request) {
            AliF2FAccount account = request.getAccount().as(AliF2FAccount.class);
            TypeReference<Map<String, String>> type = new TypeReference<>() {
            };
            Map<String, String> body = JsonUtils.jsonToObject(request.getRequestBody(), type);
            try {
                if (!AlipaySignature.rsaCheckV1(body, account.getPublicKey(),
                    account.getCharset(), account.getSignType())) {
                    log.debug("Payment|支付宝当面付|验签失败:{}", request.getRequestBody());
                    return Mono.just(
                        CallbackResult.builder().success(false).render("failure=verify")
                            .build());
                }
                String tradeStatus = body.get("trade_status");
                if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                    log.debug("Payment|支付宝当面付|支付状态未成功:{}", tradeStatus);
                    return Mono.just(
                        CallbackResult.builder().success(false).render("failure=" + tradeStatus)
                            .build());
                }
                return Mono.just(
                    CallbackResult.builder().success(true).render("success").build());
            } catch (AlipayApiException e) {
                log.error("Payment|支付宝当面付|验签异常:{}", e.getMessage(), e);
                return Mono.just(
                    CallbackResult.builder().success(false).render("error=sign_verify")
                        .build());
            }
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class AliF2FAccount extends PaymentAccount {

        private final String appId;
        private final String publicKey;
        private final String charset;
        private final String signType;
        private final String format;

        private final AlipayClient client;

        public AliF2FAccount(IAccount account) {
            super(account);
            ObjectNode config = account.getConfig();
            this.appId = JsonNodeUtils.getString(config, "appId");
            this.publicKey = JsonNodeUtils.getString(config, "publicKey");
            this.charset = JsonNodeUtils.getString(config, "charset", "UTF-8");
            this.signType = JsonNodeUtils.getString(config, "signType", "RSA2");
            this.format = JsonNodeUtils.getString(config, "format", "json");

            AlipayConfig alipayConfig = new AlipayConfig();
            //设置网关地址
            alipayConfig.setServerUrl(JsonNodeUtils.getString(config, "serverUrl",
                "https://openapi.alipay.com/gateway.do"));
            //设置应用ID
            alipayConfig.setAppId(this.getAppId());
            //设置应用私钥
            alipayConfig.setPrivateKey(JsonNodeUtils.getString(config, "privateKey"));
            //设置请求格式，固定值json
            alipayConfig.setFormat("json");
            //设置字符集
            alipayConfig.setCharset(this.charset);
            //设置签名类型
            alipayConfig.setSignType(this.signType);
            //设置支付宝公钥
            alipayConfig.setAlipayPublicKey(this.publicKey);
            //实例化客户端
            try {
                this.client = new DefaultAlipayClient(alipayConfig);
            } catch (AlipayApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
