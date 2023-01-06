package io.mvvm.halo.plugins.payment;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.Amount;
import com.wechat.pay.java.service.payments.h5.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.h5.model.H5Info;
import com.wechat.pay.java.service.payments.h5.model.PrepayRequest;
import com.wechat.pay.java.service.payments.h5.model.PrepayResponse;
import com.wechat.pay.java.service.payments.h5.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.h5.model.SceneInfo;
import io.mvvm.halo.plugins.payment.sdk.AbstractPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CancelPaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * WechatPayment.
 *
 * @author: pan
 **/
@Slf4j
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
                .flatMap(setting -> {
                    settingAtomicReference.set(setting);
                    try {
                        RSAAutoCertificateConfig.Builder builder = new RSAAutoCertificateConfig.Builder()
                                .merchantId(setting.getMerchantId())
                                .merchantSerialNumber(setting.getMerchantSerialNumber())
                                .apiV3Key(setting.getApiV3key());
                        setting.privateKey(builder);
                        H5Service service = new H5Service.Builder().config(builder.build()).build();
                        log.debug("微信支付|初始化H5成功|{}", service);
                        return Mono.just(service);
                    } catch (Exception ex) {
                        log.error("微信支付|初始化H5失败|{}", ex.getMessage(), ex);
                        return Mono.error(ex);
                    }
                })
                .map(service -> {
                    h5ServiceAtomicReference.set(service);
                    initStatusFlag.set(null != h5ServiceAtomicReference.get());
                    return initStatusFlag.get();
                });
    }

    @Override
    public Mono<CreatePaymentResponse> create(CreatePaymentRequest paymentRequest) {
        return getSettingAndService().flatMap(t2 -> {
                    WechatPaymentSetting setting = t2.setting();
                    H5Service service = t2.service();

                    try {
                        PrepayRequest request = createPrepayRequest(paymentRequest, setting);
                        PrepayResponse response = service.prepay(request);
                        return Mono.just(response);
                    } catch (Exception ex) {
                        log.error("微信支付|创建订单失败|{}, {}", paymentRequest, ex.getMessage(), ex);
                        return Mono.defer(() -> Mono.error(new RuntimeException("创建微信支付订单失败")));
                    }
                })
                .map(response -> new CreatePaymentResponse()
                        .setSuccess(StringUtils.hasLength(response.getH5Url()))
                        .setTotalFee(paymentRequest.getTotalFee())
                        .setStatus(PaymentStatus.created)
                        .setPaymentMode(PaymentMode.h5_url.name())
                        .setPaymentModeData(response.getH5Url())
                        .setOutTradeNo(paymentRequest.getOutTradeNo())
                        .setTradeNo("")
                        .setExpand(paymentRequest.getExpand()));
    }

    @Override
    public Mono<PaymentInfo> fetch(PaymentRequest paymentRequest) {
        return getSettingAndService()
                .flatMap(t2 -> {
                    WechatPaymentSetting setting = t2.setting();
                    H5Service service = t2.service();
                    try {
                        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
                        request.setOutTradeNo(paymentRequest.getOutTradeNo());
                        request.setMchid(setting.getMerchantId());
                        return Mono.just(service.queryOrderByOutTradeNo(request));
                    } catch (Exception ex) {
                        log.error("微信支付|查询订单信息失败|{}, {}", paymentRequest.getOutTradeNo(), ex.getMessage(), ex);
                        return Mono.error(new RuntimeException("查询微信支付订单信息失败"));
                    }
                })
                .map(response -> {
                    /*
                     * 交易状态，枚举值：
                     * SUCCESS：支付成功
                     * REFUND：转入退款
                     * NOTPAY：未支付
                     * CLOSED：已关闭
                     * REVOKED：已撤销（仅付款码支付会返回）
                     * USERPAYING：用户支付中（仅付款码支付会返回）
                     * PAYERROR：支付失败（仅付款码支付会返回）
                     */
                    PaymentStatus paymentStatus = PaymentStatus.created;
                    switch (response.getTradeState().name()) {
                        case "SUCCESS" -> paymentStatus = PaymentStatus.payment_successful;
                        case "REFUND" -> paymentStatus = PaymentStatus.refund_successful;
                        case "NOTPAY", "USERPAYING" -> paymentStatus = PaymentStatus.payment_processing;
                        case "CLOSED" -> paymentStatus = PaymentStatus.closed;
                        case "REVOKED", "PAYERROR" -> paymentStatus = PaymentStatus.payment_canceled;
                    }
                    return new PaymentInfo()
                            .setSuccess(true)
                            .setOutTradeNo(response.getOutTradeNo())
                            .setTradeNo(response.getTransactionId())
                            .setStatus(paymentStatus)
                            .setActualFee(response.getAmount().getPayerTotal())
                            .setTotalFee(response.getAmount().getTotal())
                            .setBackParams(response.getAttach())
                            .setExpand(paymentRequest.getExpand());
                });
    }

    @Override
    public Mono<PaymentResponse> cancel(PaymentRequest paymentRequest) {
        return getSettingAndService()
                .flatMap(t2 -> {
                    WechatPaymentSetting setting = t2.setting();
                    H5Service service = t2.service();
                    try {
                        CloseOrderRequest request = new CloseOrderRequest();
                        request.setMchid(setting.getMerchantId());
                        request.setOutTradeNo(paymentRequest.getOutTradeNo());
                        service.closeOrder(request);
                        return Mono.just(paymentRequest.getOutTradeNo());
                    } catch (Exception ex) {
                        log.error("微信支付|取消订单失败|{}, {}", paymentRequest.getOutTradeNo(), ex.getMessage(), ex);
                        return Mono.error(new RuntimeException("取消微信订单失败"));
                    }
                })
                .map(outTradeNo -> new CancelPaymentResponse()
                        .setSuccess(true)
                        .setOutTradeNo(outTradeNo)
                        .setStatus(PaymentStatus.cancel_successful)
                        .setExpand(paymentRequest.getExpand()));
    }

    @Override
    public Mono<PaymentResponse> refund(PaymentRequest request) {
        return super.refund(request);
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return fetch(() -> request.pathVariable("outTradeNo"))
                .map(info -> new AsyncNotifyResponse()
                        .setSuccess(PaymentStatus.payment_successful.check(info.getStatus().getCode()))
                        .setStatus(info.getStatus())
                        .setTradeNo(info.getTradeNo())
                        .setOutTradeNo(info.getOutTradeNo())
                        .setBackParams(info.getBackParams())
                        .setTotalFee(info.getTotalFee())
                        .setActualFee(info.getActualFee())
                        .setResponseFail(() -> Map.of("code", "FAIL", "message", "失败失败"))
                        .setResponseSuccess(() -> Map.of("code", "SUCCESS", "message", "支付成功")));
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
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setPayerClientIp(paymentRequest.getClientIp());
        H5Info h5Info = new H5Info();
        h5Info.setType("Wap");
        sceneInfo.setH5Info(h5Info);
        request.setSceneInfo(sceneInfo);
        return request;
    }

    private Mono<SettingAndService> getSettingAndService() {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无微信支付配置, 请配置后再操作"))))
                .flatMap(setting -> {
                    H5Service service = h5ServiceAtomicReference.get();
                    if (null == service) {
                        log.debug("微信支付|服务未初始化|");
                        return Mono.defer(() -> Mono.error(new RuntimeException("暂无微信支付配置, 请配置后再操作")));
                    }
                    return Mono.just(new SettingAndService(setting, service));
                });
    }

    private record SettingAndService(WechatPaymentSetting setting, H5Service service) {
        
    }

}
