package io.mvvm.halo.plugins.payment;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.Amount;
import com.wechat.pay.java.service.payments.h5.model.PrepayRequest;
import io.mvvm.halo.plugins.payment.sdk.AbstractPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

import java.util.concurrent.atomic.AtomicReference;


/**
 * WechatPayment.
 *
 * @author: pan
 **/
@Component
public class WechatPayment extends AbstractPaymentOperator {

    public static final Ref type = Ref.of("wechat");

    private final AtomicReference<H5Service> h5ServiceAtomicReference = new AtomicReference<>(null);
    private final AtomicReference<WechatPaymentSetting> settingAtomicReference = new AtomicReference<>(null);

    @Override
    public Ref type() {
        return type;
    }

    @Override
    public boolean status() {
        return initStatusFlag.get();
    }

    @Override
    public Mono<Boolean> initConfig() {
        return environmentFetcher.fetch(WechatPaymentSetting.GROUP, WechatPaymentSetting.NAME, WechatPaymentSetting.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无微信支付配置, 请配置后再操作"))))
                .map(setting -> {
                    h5ServiceAtomicReference.set(createWeChatH5Service(setting));
                    settingAtomicReference.set(setting);
                    initStatusFlag.set(true);
                    return initStatusFlag.get();
                });
    }

    private H5Service createWeChatH5Service(WechatPaymentSetting setting) {
        RSAAutoCertificateConfig.Builder builder = new RSAAutoCertificateConfig.Builder()
                .merchantId(setting.getMerchantId())
                .merchantSerialNumber(setting.getMerchantSerialNumber())
                .apiV3Key(setting.getApiV3key());
        if (null != setting.getPrivateKey()
            && setting.getPrivateKey().startsWith("file://")) {
            builder.privateKeyFromPath(setting.getPrivateKey().replace("file://", ""));
        } else {
            builder.privateKey(setting.getPrivateKey());
        }

        return new H5Service.Builder().config(builder.build()).build();
    }

    @Override
    public Mono<CreatePaymentResponse> create(CreatePaymentRequest paymentRequest) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无微信支付配置, 请配置后再操作"))))
                .map(setting -> {
                    PrepayRequest request = createPrepayRequest(paymentRequest, setting);
                    return h5ServiceAtomicReference.get().prepay(request);
                })
                .map(response -> new CreatePaymentResponse()
                        .setSuccess(StringUtils.hasLength(response.getH5Url()))
                        .setTotalFee(paymentRequest.getTotalFee())
                        .setStatus(PaymentStatus.created)
                        .setMode(PaymentMode.h5_url.name())
                        .setModeData(response.getH5Url())
                        .setOutTradeNo(paymentRequest.getOutTradeNo())
                        .setTradeNo("")
                        .setExpand(paymentRequest.getExpand()));
    }

    @Override
    public Mono<PaymentInfo> fetch(PaymentRequest request) {
        return null;
    }

    @Override
    public Mono<PaymentResponse> cancel(PaymentRequest request) {
        return super.cancel(request);
    }

    @Override
    public Mono<PaymentResponse> refund(PaymentRequest request) {
        return super.refund(request);
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return null;
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        return super.refundAsyncNotify(request);
    }

    @Override
    public void destroy() {

    }

    private PrepayRequest createPrepayRequest(CreatePaymentRequest paymentRequest, WechatPaymentSetting setting) {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(paymentRequest.getTotalFee());
        request.setAmount(amount);
        request.setAppid(setting.getAppId());
        request.setMchid(setting.getMerchantId());
        request.setDescription(paymentRequest.getTitle());
        request.setNotifyUrl(paymentRequest.getNotifyUrl());
        request.setOutTradeNo(paymentRequest.getOutTradeNo());
        request.setAttach(paymentRequest.getBackParams());
        return request;
    }

}
