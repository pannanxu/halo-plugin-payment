package io.mvvm.halo.plugins.payment.alipay;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.mvvm.halo.plugins.payment.alipay.signature.AlipayConstants;
import io.mvvm.halo.plugins.payment.alipay.signature.Rsa2Signer;
import io.mvvm.halo.plugins.payment.sdk.AbstractPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.Amount;
import io.mvvm.halo.plugins.payment.sdk.ExpandConst;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import io.mvvm.halo.plugins.payment.sdk.exception.CancelException;
import io.mvvm.halo.plugins.payment.sdk.exception.FetchException;
import io.mvvm.halo.plugins.payment.sdk.exception.RefundException;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.signature.Signer;
import io.mvvm.halo.plugins.payment.sdk.utils.GsonHelper;
import io.mvvm.halo.plugins.payment.sdk.utils.GsonUtils;
import io.mvvm.halo.plugins.payment.sdk.utils.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.infra.utils.JsonUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AliPayment.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class AliPayment extends AbstractPaymentOperator {

    private final AtomicReference<AliPaymentSetting> settingAtomicReference = new AtomicReference<>(null);
    private final AtomicReference<Signer> signerAtomicReference = new AtomicReference<>(null);

    public AliPayment() {
        super(true);
        gson = GsonUtils.getInstance(new GsonBuilder().create());
    }

    @Override
    public PaymentDescriptor getDescriptor() {
        return PaymentDescriptor.builder()
                .name("alipay")
                .title("支付宝")
                .logo("data:image/png;base64,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")
                .icon("data:image/vnd.microsoft.icon;base64,AAABAAEAICAAAAEAIACoEAAAFgAAACgAAAAgAAAAQAAAAAEAIAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP93Gy7/dxar/3gX4f93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//eBff/3cWq/94GjAAAAAAAAAAAAAAAAD/eBhq/3cW+/93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW+/94GGoAAAAA/3cbLv93Fvv/dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW+/96GzD/dxar/3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3gXq/94F9//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//eBff/3cW//93Fv//dxb//3cW//93Fv//ehz//6Rj///Iof//2Lz//97H///Mqf//uon//5dM//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//4Uu//+8jP//dxb//3cW//93Fv//dxb//51Y///z6v///////////////////////////////////////+fW//+udf//eBj//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//5hO///Ssv///Pv///////93Fv//dxb//3cW//+kY////v7///Pr///DmP//o2H//5ZL//+hX///t4P//+PQ///////////////////gyv//iTX//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//ex3//6tv///m1P///////////////////////3cW//93Fv//fyT///fx///Zvv//gCX//3cW//93Fv//dxb//3cW//93Fv//dxb//5lQ///m1P/////////////49P//oV///3cW//93Fv//dxb//3cW//93Fv//hC3//72O///07P//////////////////////////////////dxb//3cW//+qbv//9O3//34i//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3se///Jo//////////////8+v//oFz//3cW//93Fv//kEH//82q///8+v////////////////////////////////////////////93Fv//dxb//8CT///Rsf//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//+/kv/////////////7+P//snz//9q//////////////////////////////////////////////+fW///Blf//oFz//3cW//93Fv//vY7//9q///93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW///Kpf/////////////////////////////////////////////+/v//4Mr//7N+//+INP//dxb//3cW//93Fv//dxb//3cW//+cVf///fz//5JF//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//99If//tH////Dl///////////////////////////////////p2f//uIX//4gz//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3gY///l0///8+v//6Vl//94GP//dxb//3cW//93Fv//dxb//3ka//+ZUP//yqX///jz///////////////////////////////////dxv//kkX//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//5BB///07P////////Xu///Xu///0K7//9Ky///gyv//+vf/////////////////////////////8+r//+3g/////////////+zf//94GP//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//4g0///Xu////f3///////////////////////////////////v5///gyv//vpD//5VK//94F///mE7//////////////////6hq//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//+GMP//r3b//7yM///FnP//uon//6pt//+WS///ex3//3cW//93Fv//dxb//3cW//93Fv//4cz/////////////6Nj//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//+tc///////////////////lEn//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//4En///8+//////////////Ak///dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//pGT///n0///59P//+fT///n0///59P//+fT///n0///59P//+fT///n0///59P//+fT///7+/////////////+vd//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//+FL///oV7//6Fe//+hXv//oV7//6Fe//+hXv//9/H/////////////7eD//6Bd//+gXf//oF3//6Bd//+gXf//oF3//3kZ//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW///z6v/////////////l0///dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//+gXf//sXr//7F6//+xev//sXr//7F6//+xev//sXr///j0//////////////Dm//+ye///snv//7J7//+ye///snv//7J7//+ye///lUr//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//8ig///o2P//6Nj//+jY///o2P//6Nj//+jY///o2P///Pv/////////////+vf//+jX///o1///6Nf//+jX///o1///6Nf//+jX//+yfP//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW///x5//////////////l0///dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb///Hn/////////////+XT//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//8ef/////////////5dP//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//eBfh/3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//+0f///yKH//8ih//+5h///dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3gX3/93Fqv/dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//eBer/3cbLv93Fvv/dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW+/96GzAAAAAA/3gYav93Fvv/dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fvv/eBhqAAAAAAAAAAAAAAAA/3cbLv93Fqv/eBfh/3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//93Fv//dxb//3cW//94F9//dxar/3gaMAAAAAAAAAAA4AAAB8AAAAOAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAAAABwAAAA+AAAAc=")
                .userInputFormSchema(userInputFormSchema)
                .build();
    }

    @Override
    public Mono<Boolean> initConfig() {
        return getEnvironmentFetcher()
                .fetch(AliPaymentSetting.NAME, AliPaymentSetting.GROUP, AliPaymentSetting.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请配置后再操作"))))
                .map(setting -> {
                    try {
                        // 初始化 http client
                        setClient(WebClient.builder()
                                .baseUrl(setting.getServerUrl())
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF8")
                                .build());

                        // 初始化签名方式
                        signerAtomicReference.set(new Rsa2Signer(setting.getPrivateKey(), setting.getPublicKey()));

                        settingAtomicReference.set(setting);
                        initStatusFlag.set(true);
                        log.debug("支付宝|初始化成功|{}", initStatusFlag.get());
                    } catch (Exception e) {
                        log.error("支付宝|初始化支付宝配置异常|{}", e.getMessage());
                    }
                    return initStatusFlag.get();
                });
    }

    @Override
    public Mono<CreatePaymentResponse> create(CreatePaymentRequest paymentRequest) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    log.debug("Request: {}", JsonUtils.objectToJson(paymentRequest));
                    Map<String, String> contentMap = new HashMap<>(Map.of("out_trade_no", paymentRequest.getOutTradeNo(),
                            "total_amount", Amount.of(paymentRequest.getTotalFee()).toBigDecimal().toString(),
                            "subject", paymentRequest.getTitle(),
                            "product_code", "FAST_INSTANT_TRADE_PAY"));

                    Map<String, Object> expand = new HashMap<>(paymentRequest.getExpand());

                    Map<String, String> extMap = new HashMap<>();
                    extMap.put("return_url", expand.get(ExpandConst.returnUrl) + "");
                    extMap.put("notify_url", paymentRequest.getNotifyUrl());
                    try {
                        String body = buildBody("alipay.trade.page.pay", extMap, contentMap);
                        return Mono.just(new CreatePaymentResponse()
                                .setSuccess(StringUtils.hasLength(body))
                                .setPaymentMode(PaymentMode.h5_url.name())
                                .setPaymentModeData(setting.getServerUrl() + "?" + body)
                                .setStatus(PaymentStatus.created)
                                .setExpand(expand)
                                .setOutTradeNo(paymentRequest.getOutTradeNo())
                                .setTotalFee(paymentRequest.getTotalFee()));
                    } catch (Exception e) {
                        log.error("支付宝|创建支付宝订单异常|{}", e.getMessage(), e);
                        return Mono.just(new CreatePaymentResponse()
                                .setSuccess(false)
                                .setPaymentMode(PaymentMode.h5_url.name())
                                .setPaymentModeData(null)
                                .setStatus(PaymentStatus.created)
                                .setExpand(expand)
                                .setOutTradeNo(paymentRequest.getOutTradeNo())
                                .setTotalFee(paymentRequest.getTotalFee()));
                    }
                });
    }

    @Override
    public Mono<PaymentInfo> fetch(PaymentRequest request) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    Map<String, String> contentMap = Map.of("out_trade_no", request.getOutTradeNo());
                    String body = buildBody("alipay.trade.query", Map.of(), contentMap);
                    return postMono(body, "alipay_trade_query_response");
                })
                .onErrorResume(BaseException.class,
                        err -> Mono.error(new FetchException(err.getCode(), err.getMessage())))
                .flatMap(wrapper -> {
                    /*
                     * 交易状态：
                     * WAIT_BUYER_PAY（交易创建，等待买家付款）、
                     * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
                     * TRADE_SUCCESS（交易支付成功）、
                     * TRADE_FINISHED（交易结束，不可退款）
                     */
                    String tradeStatus = wrapper.getAsString("trade_status");
                    PaymentStatus status = PaymentStatus.created;
                    switch (tradeStatus) {
                        case "TRADE_CLOSED", "TRADE_FINISHED" -> status = PaymentStatus.closed;
                        case "TRADE_SUCCESS" -> status = PaymentStatus.payment_successful;
                    }
                    PaymentInfo paymentInfo = new PaymentInfo()
                            .setSuccess(true)
                            .setStatus(status)
                            .setOutTradeNo(wrapper.getAsString("out_trade_no"))
                            .setTradeNo(wrapper.getAsString("trade_no"))
                            .setTotalFee(new BigDecimal(wrapper.getAsString("total_amount")).multiply(new BigDecimal(100)).intValue())
                            .setActualFee(new BigDecimal(wrapper.getAsString("receipt_amount")).multiply(new BigDecimal(100)).intValue())
                            .setPaySuccessTime(wrapper.getAsDate("send_pay_date", "yyyy-MM-dd HH:mm:ss"));
                    return Mono.just(paymentInfo);
                });
    }

    @Override
    public Mono<PaymentResponse> cancel(PaymentRequest request) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    Map<String, String> contentMap = Map.of("out_trade_no", request.getOutTradeNo());
                    String body = buildBody("alipay.trade.close", Map.of(), contentMap);
                    return postMono(body, "alipay_trade_close_response");
                })
                .onErrorResume(BaseException.class,
                        err -> Mono.error(new CancelException(err.getCode(), err.getMessage())))
                .flatMap(wrapper -> Mono.just(new PaymentInfo()
                        .setSuccess(true)
                        .setStatus(PaymentStatus.cancel_successful)
                        .setOutTradeNo(wrapper.getAsString("out_trade_no"))));
    }

    @Override
    public Mono<PaymentResponse> refund(PaymentRequest request) {
        return Mono.justOrEmpty(settingAtomicReference.get())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无支付宝配置, 请初始化后再操作"))))
                .flatMap(setting -> {
                    Map<String, String> contentMap = Map.of("out_trade_no", request.getOutTradeNo());
                    String body = buildBody("alipay.trade.refund", Map.of(), contentMap);
                    return postMono(body, "alipay_trade_refund_response");
                })
                .onErrorResume(BaseException.class,
                        err -> Mono.error(new RefundException(err.getCode(), err.getMessage())))
                .flatMap(wrapper -> Mono.just(new PaymentInfo()
                        .setSuccess(true)
                        .setStatus(PaymentStatus.refund_successful)
                        .setOutTradeNo(wrapper.getAsString("out_trade_no"))));
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

    protected Mono<GsonHelper> postMono(String body, String responseKey) {
        return getClient().post()
                .bodyValue(body)
                .exchangeToMono(response -> response.bodyToMono(String.class))
                .flatMap(response -> {
                    log.debug("支付宝|响应参数|{}", response);
                    if (!StringUtils.hasLength(response)) {
                        log.debug("支付宝|响应参数为空|{}", response);
                        return Mono.error(new BaseException("支付宝请求响应参数为空"));
                    }

                    try {
                        JsonObject jsonObject = GsonUtils.parse(response);
                        JsonElement alipayTradeCloseResponse = jsonObject.get(responseKey);
                        if (alipayTradeCloseResponse.isJsonNull()) {
                            log.debug("支付宝|响应参数错误|{}", response);
                            return Mono.error(new BaseException("支付宝订单退款响应参数错误"));
                        }
                        GsonHelper wrapper = GsonHelper.of(alipayTradeCloseResponse.getAsJsonObject());
                        String code = wrapper.getAsString("code");
                        String subCode = wrapper.getAsString("sub_code");
                        if (StringUtils.hasLength(subCode)) {
                            log.debug("支付宝|响应code未成功|{}", response);
                            return Mono.error(new BaseException(subCode, wrapper.getAsString("sub_msg")));
                        }
                        log.debug("支付宝|响应成功|{}", wrapper);
                        return Mono.just(wrapper);
                    } catch (Exception ex) {
                        log.error("支付宝|解析响应结果失败|{}", response, ex);
                        return Mono.error(new BaseException("解析支付宝响应结果失败"));
                    }
                });
    }

    /**
     * 构建请求体
     *
     * @param method        请求方法
     * @param extParams     扩展参数
     * @param bizContentMap 请求参数
     * @return queryParam
     */
    String buildBody(String method,
                     Map<String, String> extParams,
                     Map<String, String> bizContentMap) {
        Signer signer = signerAtomicReference.get();
        AliPaymentSetting setting = settingAtomicReference.get();

        String content = JsonUtils.objectToJson(bizContentMap);

        Map<String, String> params = new HashMap<>();
        params.put("app_id", setting.getAppId());
        params.put("method", method);
        params.put("format", "json");
        params.put("charset", "utf-8");
        params.put("sign_type", "RSA2");
        params.putAll(extParams);

        long timestamp = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat(AlipayConstants.DATE_TIME_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone(AlipayConstants.DATE_TIMEZONE));
        String format = df.format(new Date(timestamp));
        params.put("timestamp", format);
        params.put("version", "1.0");
        params.put("biz_content", content);

        String body = MapUtils.sortToString(params);
        params.put("sign", signer.sign(body));
        return MapUtils.getUrlParam(params);
    }
}
