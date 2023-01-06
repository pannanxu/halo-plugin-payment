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
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
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
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * WechatPayment.
 *
 * @author: pan
 **/
@Slf4j
public class WechatPayment extends AbstractPaymentOperator {

    private final AtomicReference<H5Service> h5ServiceAtomicReference = new AtomicReference<>(null);
    private final AtomicReference<WechatPaymentSetting> settingAtomicReference = new AtomicReference<>(null);

    @Override
    public PaymentDescriptor getDescriptor() {
        return PaymentDescriptor.builder()
                .name("wechat")
                .title("微信支付")
                .logo("data:image/png;base64,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")
                .icon("data:image/vnd.microsoft.icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAABILAAASCwAAAAAAAAAAAAAAAAAAGa0aABmtGg4ZrRoOGa0aABmtGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABmtGgAZrRpGGa0arBmtGkEZrRobGa0aOBmtGkwZrRpNGa0aORmtGhoZrRoDGa0aAAAAAAAAAAAAAAAAAAAAAAAZrRoAGa0aKBmtGucZrRrwGa0a3BmtGvIZrRr6Ga0a+xmtGvMZrRrYGa0alxmtGjcZrRoCGa0aAAAAAAAZrRoAGa0aBhmtGnMZrRr1Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRroGa0adBmtGgkZrRoAGa0aAhmtGnEZrRr2Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGvgZrRp5Ga0aAxmtGkEZrRrmGa0a/xmtGv8ZrRr+Ga0a8RmtGvsZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a6hmtGkkZrRqlGa0a/xmtGv8ZrRr/Ga0a5RmtGkwZrRpzGa0a3hmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRqvGa0a3xmtGv8ZrRr/Ga0a/xmtGpkZrRoDGa0aABmtGicZrRqWGa0a7hmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a5xmtGusZrRr/Ga0a/xmtGu0ZrRo9Ga0aPBmtGmcZrRohGa0aAxmtGj8ZrRq0Ga0a+BmtGv8ZrRr/Ga0a/xmtGvIZrRrQGa0a/xmtGv8ZrRrLGa0agRmtGukZrRr+Ga0a3RmtGosZrRowGa0aEBmtGlsZrRrNGa0a/RmtGv8ZrRrYGa0agxmtGv0ZrRr/Ga0a+hmtGvsZrRr/Ga0a/xmtGv8ZrRr/Ga0a6hmtGqIZrRpDGa0aJRmtGnsZrRrhGa0ajBmtGiAZrRrHGa0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a9BmtGrcZrRpdGa0aOxmtGhoZrRoAGa0aPBmtGtcZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr/Ga0a1BmtGiwZrRoAGa0aABmtGgAZrRowGa0arRmtGvQZrRr/Ga0a/xmtGv8ZrRr/Ga0a/xmtGv8ZrRr2Ga0asRmtGjQZrRoAGa0aAAAAAAAZrRoAGa0aABmtGgsZrRpFGa0ajRmtGrsZrRrPGa0azxmtGr0ZrRqQGa0aSBmtGgwZrRoAGa0aAAAAAAAAAAAAAAAAAAAAAAAAAAAAGa0aABmtGgEZrRoKGa0aFBmtGhUZrRoKGa0aARmtGgAAAAAAAAAAAAAAAAAAAAAAz/8AAMAPAADAAwAAgAEAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAgAEAAMADAADgBwAA+B8AAA==")
                .build();
    }

    @Override
    public boolean status() {
//        return initStatusFlag.get();
        return true;
    }

    @Override
    public Mono<Boolean> initConfig() {
        return getEnvironmentFetcher()
                .fetch(WechatPaymentSetting.GROUP, WechatPaymentSetting.NAME, WechatPaymentSetting.class)
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
//                            .setPaySuccessTime(response.getSuccessTime());
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
