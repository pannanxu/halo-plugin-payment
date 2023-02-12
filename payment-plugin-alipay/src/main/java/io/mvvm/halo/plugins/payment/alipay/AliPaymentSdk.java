package io.mvvm.halo.plugins.payment.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import io.mvvm.halo.plugins.payment.sdk.AbstractPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.mvvm.halo.plugins.payment.sdk.ExpandConst;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentSetting;
import io.mvvm.halo.plugins.payment.sdk.enums.Endpoint;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.exception.ExceptionCode;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.infra.utils.JsonUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * AliPayment SDK 的实现.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class AliPaymentSdk extends AbstractPaymentOperator {

    private final AtomicReference<AliPaymentSetting> settingAtomicReference = new AtomicReference<>();
    private final AtomicReference<AlipayClient> alipayClientAtomicReference = new AtomicReference<>();

    public AliPaymentSdk(PluginWrapper pluginWrapper) {
        super(pluginWrapper, true);
    }

    @Override
    public PaymentDescriptor getDescriptor() {
        return PaymentDescriptor.builder()
                .name("alipay")
                .title("支付宝")
                .userInputFormSchema(userInputFormSchema)
                .endpoint(Set.of(Endpoint.pc.name(), Endpoint.wap.name()))
                .build();
    }

    @Override
    public Mono<Boolean> initConfig(ServerRequest request) {
        return getEnvironmentFetcher()
                .fetch(AliPaymentSetting.NAME, AliPaymentSetting.GROUP, AliPaymentSetting.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请配置后再操作"))))
                .flatMap(setting -> {
                    try {
                        log.debug("initConfig: {}", JsonUtils.objectToJson(setting));
                        AlipayConfig config = new AlipayConfig();
                        config.setServerUrl(setting.getServerUrl());
                        config.setAppId(setting.getAppId());
                        config.setPrivateKey(setting.getPrivateKey());
                        config.setAlipayPublicKey(setting.getAlipayPublicKey());
                        config.setEncryptKey(setting.getEncryptKey());
                        if (AliPaymentSetting.CERT_MODE.equals(setting.getMode())) {
                            if (setting.getAppCert().startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
                                config.setAppCertPath(setting.getAppCert().replaceAll(PaymentSetting.LOCAL_FILE_PREFIX, ""));
                            } else {
                                config.setAppCertContent(setting.getAppCert());
                            }
                            if (setting.getAlipayPublicCert().startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
                                config.setAlipayPublicCertPath(setting.getAlipayPublicCert().replaceAll(PaymentSetting.LOCAL_FILE_PREFIX, ""));
                            } else {
                                config.setAlipayPublicCertContent(setting.getAlipayPublicCert());
                            }
                            if (setting.getAlipayRootCert().startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
                                config.setRootCertPath(setting.getAlipayRootCert().replaceAll(PaymentSetting.LOCAL_FILE_PREFIX, ""));
                            } else {
                                config.setRootCertContent(setting.getAlipayRootCert());
                            }
                        }
                        AlipayClient alipayClient = new DefaultAlipayClient(config);
                        alipayClientAtomicReference.set(alipayClient);
                        settingAtomicReference.set(setting);
                        initStatusFlag.set(true);
                        log.debug("支付宝|初始化成功|{}", initStatusFlag.get());
                    } catch (Exception e) {
                        log.error("支付宝|初始化支付宝配置异常|{}", e.getMessage());
                        return Mono.error(e);
                    }
                    return Mono.just(initStatusFlag.get());
                });
    }

    @Override
    public Mono<CreatePaymentResponse> create(CreatePaymentRequest request) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
                    payRequest.setNotifyUrl(request.getNotifyUrl());
                    if (null != request.getExpand()) {
                        String returnUrl = request.getExpand().get(ExpandConst.returnUrl);
                        payRequest.setReturnUrl(returnUrl);
                    }
                    AlipayTradePagePayModel model = new AlipayTradePagePayModel();
                    model.setOutTradeNo(request.getOutTradeNo());
                    model.setTotalAmount(request.getMoney().toYuanStr());
                    model.setSubject(request.getTitle());
                    model.setProductCode("FAST_INSTANT_TRADE_PAY");
                    model.setBody(request.getDescription());
                    model.setPassbackParams(request.getBiz().getBackParams());
                    payRequest.setBizModel(model);
                    try {
                        AlipayTradePagePayResponse response = alipayClientAtomicReference.get()
                                .certificateExecute(payRequest);
                        return Mono.just(new CreatePaymentResponse()
                                .setStatus(PaymentStatus.created)
                                .setPaymentModeData(response.getBody())
                                .setSuccess(response.isSuccess())
                                .setExpand(request.getExpand())
                                .setMoney(request.getMoney())
                                .setOutTradeNo(request.getOutTradeNo())
                                .setPaymentMode(PaymentMode.html_form.name()));
                    } catch (Exception e) {
                        log.error("支付宝|创建订单未知异常|{}", e.getMessage(), e);
                        return Mono.just(CreatePaymentResponse.onError(e));
                    }
                })
                .log("payment.plugin.alipay", log.isDebugEnabled() ? Level.INFO : Level.OFF);
    }

    @Override
    public Mono<PaymentInfo> fetch(PaymentRequest request) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
                    AlipayTradeQueryModel model = new AlipayTradeQueryModel();
                    model.setOutTradeNo(request.getOutTradeNo());
                    queryRequest.setBizModel(model);
                    try {
                        AlipayTradeQueryResponse response = exec(setting, queryRequest);
                        if (!response.isSuccess()) {
                            return Mono.just(PaymentInfo.onError(ExceptionCode.biz_error.name(), "查询支付宝订单失败"));
                        }
                        PaymentStatus status = null;
                        switch (response.getTradeStatus()) {
                            case "WAIT_BUYER_PAY" -> status = PaymentStatus.created;
                            case "TRADE_CLOSED", "TRADE_FINISHED" -> status = PaymentStatus.closed;
                            case "TRADE_SUCCESS" -> status = PaymentStatus.payment_successful;
                        }
                        return Mono.just(new PaymentInfo()
                                .setSuccess(true)
                                .setStatus(status)
                                .setOutTradeNo(response.getOutTradeNo())
                                .setTradeNo(response.getTradeNo())
                                .setMoney(Amount.ofYuan(response.getTotalAmount()).setCurrency(response.getPayCurrency()))
                                .setActualMoney(Amount.ofYuan(response.getBuyerPayAmount(), "0.00"))
                                .setPaySuccessTime(response.getSendPayDate())
                                .setBackParams(response.getPassbackParams()));
                    } catch (Exception e) {
                        log.error("支付宝|查询订单失败|{}", e.getMessage(), e);
                        return Mono.just(PaymentInfo.onError(e));
                    }
                });
    }

    @Override
    public Mono<PaymentResponse> cancel(PaymentRequest request) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    AlipayTradeCloseRequest closeRequest = new AlipayTradeCloseRequest();
                    AlipayTradeCloseModel model = new AlipayTradeCloseModel();
                    model.setOutTradeNo(request.getOutTradeNo());
                    closeRequest.setBizModel(model);
                    try {
                        AlipayTradeCloseResponse response = exec(setting, closeRequest);
                        if (!response.isSuccess()) {
                            return Mono.just(PaymentInfo.onError(ExceptionCode.biz_error.name(), response.getMsg()));
                        }
                        return Mono.just(new PaymentInfo()
                                .setOutTradeNo(response.getOutTradeNo())
                                .setSuccess(response.isSuccess())
                                .setStatus(PaymentStatus.closed)
                                .setTradeNo(response.getTradeNo()));
                    } catch (Exception e) {
                        log.error("支付宝|关闭订单失败|{}", e.getMessage(), e);
                        return Mono.just(PaymentInfo.onError(e));
                    }
                });
    }

    @Override
    public Mono<RefundPaymentResponse> refund(RefundPaymentRequest request) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    try {
                        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
                        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
                        model.setOutTradeNo(request.getOutTradeNo());
                        model.setRefundAmount(request.getRefundMoney().toYuanStr());
                        model.setRefundReason(request.getRefundReason());
                        model.setOutRequestNo(request.getRefundNo());
                        model.setQueryOptions(List.of("refund_detail_item_list"));
                        refundRequest.setBizModel(model);
                        AlipayTradeRefundResponse response = exec(setting, refundRequest);

                        if (!response.isSuccess()) {
                            return Mono.just(RefundPaymentResponse.onError(ExceptionCode.biz_error.name(), response.getMsg()));
                        }
                        if (!"Y".equals(response.getFundChange())) {
                            FetchRefundPaymentRequest fetchRefundPaymentRequest = new FetchRefundPaymentRequest();
                            fetchRefundPaymentRequest.setRefundNo(request.getRefundNo());
                            fetchRefundPaymentRequest.setOutTradeNo(request.getOutTradeNo());
                            fetchRefundPaymentRequest.setExpand(request.getExpand());
                            return fetchRefund(fetchRefundPaymentRequest);
                        }
                        return Mono.just(new RefundPaymentResponse()
                                .setOutTradeNo(response.getOutTradeNo())
                                .setSuccess(true)
                                .setStatus(PaymentStatus.refund_successful)
                                .setTradeNo(response.getTradeNo())
                                .setRefundMoney(Amount.ofYuan(response.getSendBackFee(), "0.00")));
                    } catch (Exception e) {
                        log.error("支付宝|订单退款失败|{}", e.getMessage(), e);
                        return Mono.just(RefundPaymentResponse.onError(e));
                    }
                });
    }

    @Override
    public Mono<RefundPaymentResponse> fetchRefund(FetchRefundPaymentRequest request) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    try {
                        AlipayTradeFastpayRefundQueryRequest refundQueryRequest = new AlipayTradeFastpayRefundQueryRequest();
                        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
                        model.setOutRequestNo(request.getRefundNo());
                        model.setOutTradeNo(request.getOutTradeNo());
                        refundQueryRequest.setBizModel(model);
                        AlipayTradeFastpayRefundQueryResponse response = exec(setting, refundQueryRequest);
                        if (!response.isSuccess()) {
                            return Mono.just(RefundPaymentResponse.onError(ExceptionCode.biz_error.name(), response.getMsg()));
                        }
                        PaymentStatus status;
                        if (!"REFUND_SUCCESS".equals(response.getRefundStatus())) {
                            status = PaymentStatus.refund_failed;
                        } else {
                            status = PaymentStatus.refund_successful;
                        }

                        return Mono.just(new RefundPaymentResponse()
                                .setOutTradeNo(response.getOutTradeNo())
                                .setSuccess(true)
                                .setStatus(status)
                                .setTradeNo(response.getTradeNo())
                                .setRefundMoney(Amount.ofYuan(response.getRefundAmount(), "0.00"))
                                .setRefundNo(response.getOutRequestNo())
                                .setMoney(Amount.ofYuan(response.getTotalAmount(), "0.00")));
                    } catch (Exception e) {
                        log.error("支付宝|订单查询退款失败|{}", e.getMessage(), e);
                        return Mono.just(RefundPaymentResponse.onError(e));
                    }
                });
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return fetch(() -> request.pathVariable("name"))
                .map(response -> new AsyncNotifyResponse()
                        .setSuccess(response.isSuccess())
                        .setMoney(response.getMoney())
                        .setStatus(response.getStatus())
                        .setSuccess(response.isSuccess())
                        .setOutTradeNo(response.getOutTradeNo())
                        .setTradeNo(response.getTradeNo())
                        .setActualFee(response.getActualMoney())
                        .setBackParams(response.getBackParams())
                        .setResponseSuccess(() -> "success")
                        .setResponseFail(() -> "fail"));
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        FetchRefundPaymentRequest fetchRefundPaymentRequest = new FetchRefundPaymentRequest()
                .setRefundNo(request.pathVariable("refundNo"))
                .setOutTradeNo(request.pathVariable("name"));
        return fetchRefund(fetchRefundPaymentRequest)
                .map(response -> new AsyncNotifyResponse()
                        .setSuccess(response.isSuccess())
                        .setStatus(response.getStatus())
                        .setMoney(response.getMoney())
                        .setOutTradeNo(response.getOutTradeNo())
                        .setTradeNo(response.getTradeNo())
                        .setActualFee(response.getRefundMoney())
                        .setResponseSuccess(() -> "success")
                        .setResponseFail(() -> "fail"));
    }

    @Override
    public void destroy() {
        initStatusFlag.set(false);
        settingAtomicReference.set(null);
        alipayClientAtomicReference.set(null);
    }

    /**
     * 根据插件设置去决定使用证书还是公钥
     */
    <T extends AlipayResponse> T exec(AliPaymentSetting setting, AlipayRequest<T> request) {
        try {
            AlipayClient alipayClient = alipayClientAtomicReference.get();
            T response;
            if (setting.isCertMode()) {
                response = alipayClient.certificateExecute(request);
            } else {
                response = alipayClient.execute(request);
            }
            return response;
        } catch (Exception ex) {
            log.error("支付宝|请求异常|{}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

}
