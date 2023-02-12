package io.mvvm.halo.plugins.payment.wechat;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.Amount;
import com.wechat.pay.java.service.payments.h5.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.h5.model.H5Info;
import com.wechat.pay.java.service.payments.h5.model.PrepayRequest;
import com.wechat.pay.java.service.payments.h5.model.PrepayResponse;
import com.wechat.pay.java.service.payments.h5.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.h5.model.SceneInfo;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.RefundNotification;
import io.mvvm.halo.plugins.payment.sdk.AbstractPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.enums.Endpoint;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.exception.CancelException;
import io.mvvm.halo.plugins.payment.sdk.exception.CreateException;
import io.mvvm.halo.plugins.payment.sdk.exception.FetchException;
import io.mvvm.halo.plugins.payment.sdk.exception.RefundException;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CancelPaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


/**
 * 微信支付能力.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class WechatPayment extends AbstractPaymentOperator {

    private final AtomicReference<H5Service> h5ServiceAtomicReference = new AtomicReference<>(null);
    private final AtomicReference<RefundService> refundServiceAtomicReference = new AtomicReference<>(null);
    private final AtomicReference<WechatPaymentSetting> settingAtomicReference = new AtomicReference<>(null);
    private final AtomicReference<RSAAutoCertificateConfig> configAtomicReference = new AtomicReference<>(null);

    public WechatPayment(PluginWrapper pluginWrapper) {
        super(pluginWrapper, false);
    }

    @Override
    public PaymentDescriptor getDescriptor() {
        return PaymentDescriptor.builder()
                .name("wechat")
                .title("微信支付")
                .userInputFormSchema(userInputFormSchema)
                .endpoint(Set.of(Endpoint.pc.name(), Endpoint.wap.name()))
                .build();
    }

    @Override
    public Mono<Boolean> initConfig(ServerRequest request) {
        return getEnvironmentFetcher()
                .fetch(WechatPaymentSetting.GROUP, WechatPaymentSetting.NAME, WechatPaymentSetting.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无微信支付配置, 请配置后再操作"))))
                .flatMap(setting -> {
                    try {
                        RSAAutoCertificateConfig.Builder builder = new RSAAutoCertificateConfig.Builder()
                                .merchantId(setting.getMerchantId())
                                .merchantSerialNumber(setting.getMerchantSerialNumber())
                                .apiV3Key(setting.getApiV3key());
                        setting.privateKey(builder);
                        RSAAutoCertificateConfig config = builder.build();
                        configAtomicReference.set(config);
                        h5ServiceAtomicReference.set(new H5Service.Builder().config(config).build());
                        refundServiceAtomicReference.set(new RefundService.Builder().config(config).build());
                        settingAtomicReference.set(setting);
                        initStatusFlag.set(null != h5ServiceAtomicReference.get());
                        log.debug("微信支付|初始化H5成功|");
                        return Mono.just(initStatusFlag.get());
                    } catch (Exception ex) {
                        log.error("微信支付|初始化H5失败|{}", ex.getMessage(), ex);
                        return Mono.error(ex);
                    }
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
                        return Mono.error(new CreateException("创建微信支付订单失败"));
                    }
                })
                .map(response -> new CreatePaymentResponse()
                        .setSuccess(StringUtils.hasLength(response.getH5Url()))
                        .setMoney(paymentRequest.getMoney())
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
                        return Mono.error(new FetchException("查询微信支付订单信息失败"));
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
                        case "CLOSED", "REVOKED", "PAYERROR" -> paymentStatus = PaymentStatus.closed;
                    }
                    return new PaymentInfo()
                            .setSuccess(true)
                            .setOutTradeNo(response.getOutTradeNo())
                            .setTradeNo(response.getTransactionId())
                            .setStatus(paymentStatus)
                            .setActualMoney(io.mvvm.halo.plugins.payment.sdk.Amount.of(response.getAmount().getPayerTotal(), 0))
                            .setMoney(io.mvvm.halo.plugins.payment.sdk.Amount.of(response.getAmount().getTotal(), 0))
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
                        return Mono.error(new CancelException("取消微信订单失败"));
                    }
                })
                .map(outTradeNo -> new CancelPaymentResponse()
                        .setSuccess(true)
                        .setOutTradeNo(outTradeNo)
                        .setStatus(PaymentStatus.closed)
                        .setExpand(paymentRequest.getExpand()));
    }

    @Override
    public Mono<RefundPaymentResponse> refund(RefundPaymentRequest refundPaymentRequest) {
        return getSettingAndService()
                .flatMap(t2 -> {
                    RefundService refundService = refundServiceAtomicReference.get();
                    try {
                        CreateRequest request = new CreateRequest();
                        request.setOutRefundNo(refundPaymentRequest.getRefundNo());
                        request.setOutTradeNo(refundPaymentRequest.getOutTradeNo());
                        request.setReason(refundPaymentRequest.getRefundReason());
                        request.setNotifyUrl(refundPaymentRequest.getRefundNotifyUrl());
                        AmountReq amountReq = new AmountReq();
                        amountReq.setRefund(refundPaymentRequest.getRefundMoney().getTotal().longValue());
                        amountReq.setTotal(refundPaymentRequest.getMoney().getTotal().longValue());
                        amountReq.setCurrency(refundPaymentRequest.getRefundMoney().getCurrency());
                        request.setAmount(amountReq);
                        return Mono.just(refundService.create(request));
                    } catch (Exception ex) {
                        log.error("微信支付|订单退款申请失败|{}, {}", refundPaymentRequest.getOutTradeNo(), ex.getMessage(), ex);
                        return Mono.error(new RefundException("微信订单退款申请失败"));
                    }
                })
                .map(response -> {
                    PaymentStatus status = PaymentStatus.refund_processing;
                    switch (response.getStatus()) {
                        case SUCCESS -> status = PaymentStatus.refund_successful;
                        case CLOSED -> status = PaymentStatus.closed;
                        case ABNORMAL -> status = PaymentStatus.refund_failed;
                    }
                    return new RefundPaymentResponse()
                            .setSuccess(true)
                            .setStatus(status)
                            .setRefundNo(response.getOutRefundNo())
                            .setOutTradeNo(response.getOutTradeNo())
                            .setTradeNo(response.getTransactionId())
                            .setRefundMoney(io.mvvm.halo.plugins.payment.sdk.Amount.of(response.getAmount().getTotal().intValue()));
                });
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
                        .setMoney(info.getMoney())
                        .setActualFee(info.getActualMoney())
                        .setResponseFail(() -> Map.of("code", "FAIL", "message", "失败失败"))
                        .setResponseSuccess(() -> Map.of("code", "SUCCESS", "message", "支付成功")));
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        // 构造 RequestParam
        return request.bodyToMono(String.class)
                .flatMap(body -> {
                    RequestParam requestParam = new RequestParam.Builder()
                            .serialNumber(request.headers().firstHeader("Wechatpay-Serial"))
                            .nonce(request.headers().firstHeader("Wechatpay-Nonce"))
                            .signature(request.headers().firstHeader("Wechatpay-Signature"))
                            .timestamp(request.headers().firstHeader("Wechatpay-Timestamp"))
                            // 若未设置signType，默认值为 WECHATPAY2-SHA256-RSA2048
                            .signType(request.headers().firstHeader("Wechatpay-Signature-Type"))
                            .body(body)
                            .build();
                    NotificationParser parser = new NotificationParser(configAtomicReference.get());
                    RefundNotification notification = parser.parse(requestParam, RefundNotification.class);
                    log.debug("wechat refund notification: {}", notification);
                    return Mono.empty();
                });

//        RefundPaymentRequest refundRequest = new RefundPaymentRequest();
//        return refund(refundRequest)
//                .map(response -> new AsyncNotifyResponse()
//                        .setSuccess(RefundPaymentResponse.Status.successful.equals(response.getRefundStatus()))
//                        .setStatus(response.getStatus())
//                        .setTradeNo(response.getTradeNo())
//                        .setOutTradeNo(response.getOutTradeNo())
//                        .setMoney(response.getMoney())
//                        .setResponseFail(() -> Map.of("code", "FAIL", "message", "失败失败"))
//                        .setResponseSuccess(() -> Map.of("code", "SUCCESS", "message", "支付成功")));
    }

    @Override
    public void destroy() {
        initStatusFlag.set(false);
        settingAtomicReference.set(null);
        h5ServiceAtomicReference.set(null);
    }

    private PrepayRequest createPrepayRequest(CreatePaymentRequest paymentRequest, WechatPaymentSetting setting) {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(paymentRequest.getMoney().getTotal());
        request.setAmount(amount);
        request.setAppid(setting.getAppId());
        request.setMchid(setting.getMerchantId());
        request.setDescription(paymentRequest.getTitle());
        request.setNotifyUrl(paymentRequest.getNotifyUrl());
        request.setOutTradeNo(paymentRequest.getOutTradeNo());
        request.setAttach(paymentRequest.getBiz().getBackParams());
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setPayerClientIp(paymentRequest.getCreator().getIpAddress());
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
