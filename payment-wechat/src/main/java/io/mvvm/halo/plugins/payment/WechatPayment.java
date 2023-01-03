package io.mvvm.halo.plugins.payment;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.Amount;
import com.wechat.pay.java.service.payments.h5.model.PrepayRequest;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;
import io.mvvm.halo.plugins.payment.sdk.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.simple.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.simple.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.simple.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.simple.PaymentInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * WechatPayment.
 *
 * @author: pan
 **/
@Component
public class WechatPayment implements IPaymentOperator {

    public static final Ref type = Ref.of("wechat");

    private final AtomicBoolean initStatusFlag = new AtomicBoolean(false);
    private final AtomicReference<H5Service> h5ServiceAtomicReference = new AtomicReference<>(null);
    private final AtomicReference<WechatPaymentSetting> settingAtomicReference = new AtomicReference<>(null);
    private final PayEnvironmentFetcher fetcher;
    private final PaymentRegister register;

    public WechatPayment(PayEnvironmentFetcher fetcher, PaymentRegister register) {
        this.fetcher = fetcher;
        this.register = register;
        register.register(this);
    }

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
        return fetcher.fetch(WechatPaymentSetting.GROUP, WechatPaymentSetting.NAME, WechatPaymentSetting.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无微信支付配置, 请配置后再操作"))))
                .map(setting -> {
                    h5ServiceAtomicReference.set(createWeChatH5Service(setting));
                    settingAtomicReference.set(setting);
                    initStatusFlag.set(true);
                    return initStatusFlag.get();
                });
    }

    private H5Service createWeChatH5Service(WechatPaymentSetting setting) {
        Config config = new RSAAutoCertificateConfig.Builder()
                .merchantId(setting.getMerchantId())
                .privateKey(setting.getPrivateKey())
                .merchantSerialNumber(setting.getMerchantSerialNumber())
                .apiV3Key(setting.getApiV3key())
                .build();
        return new H5Service.Builder().config(config).build();
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
                        .setTotalFee(paymentRequest.getTotalFee())
                        .setStatus(PaymentStatus.created)
                        .setMode("h5_url")
                        .setModeData(response.getH5Url())
                        .setOutTradeNo(paymentRequest.getOutTradeNo())
                        .setTradeNo("")
                        .setExpand(paymentRequest.getExpand()));
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
        return request;
    }

    @Override
    public Mono<PaymentInfo> fetch(PaymentRequest request) {
        return null;
    }

    @Override
    public Mono<AsyncNotifyResponse> asyncNotify(ServerRequest request) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
