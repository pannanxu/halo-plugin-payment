package io.mvvm.halo.plugins.payment.rule;

import io.mvvm.halo.plugins.payment.sdk.ExpandConst;
import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.PaymentSetting;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import io.mvvm.halo.plugins.payment.sdk.exception.ExceptionCode;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * 黑名单.
 *
 * @author: pan
 **/
@Slf4j
public class BlackListRule extends BasePaymentRule {

    private final PayEnvironmentFetcher fetcher;

    public BlackListRule(IPayment payment, PayEnvironmentFetcher fetcher) {
        super(payment);
        this.fetcher = fetcher;
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        return black(request).flatMap(e -> super.create(request));
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        return black(request).flatMap(e -> super.fetch(request));
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        return black(request).flatMap(e -> super.cancel(request));
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request) {
        return black(request).flatMap(e -> super.refund(request));
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request) {
        return black(request).flatMap(e -> super.fetchRefund(request));
    }

    private Mono<Boolean> black(PaymentRequest request) {
        String black = request.getExpandAsString(ExpandConst.blackListRuleKey);
        if (!StringUtils.hasLength(black)) {
            return Mono.just(Boolean.TRUE);
        }
        return fetcher.fetchPaymentConfig(PaymentSetting.basic)
                .flatMap(setting -> {
                    if (null != setting.getBlackListIp() && setting.getBlackListIp().contains(black)) {
                        return Mono.error(new BaseException(ExceptionCode.black_list, "您已被拉入黑名单列表"));
                    }
                    return Mono.just(Boolean.TRUE);
                });
    }

}
