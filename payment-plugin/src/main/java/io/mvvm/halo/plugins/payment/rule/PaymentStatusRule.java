package io.mvvm.halo.plugins.payment.rule;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentExtension;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import io.mvvm.halo.plugins.payment.sdk.exception.CancelException;
import io.mvvm.halo.plugins.payment.sdk.exception.CreateException;
import io.mvvm.halo.plugins.payment.sdk.exception.FetchException;
import io.mvvm.halo.plugins.payment.sdk.exception.RefundException;
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
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * 支付开关配置.
 *
 * @author: pan
 **/
@Slf4j
public class PaymentStatusRule extends BasePaymentRule {
    
    public PaymentStatusRule(IPayment payment) {
        super(payment);
    }

    @Override
    public Mono<Boolean> status() {
        return payment.status()
                .flatMap(status -> {
                    if (status) {
                        return client.fetch(PaymentExtension.class, payment.getDescriptor().getName())
                                .map(e -> e.getSpec().getEnabled())
                                .switchIfEmpty(Mono.just(false));
                    }
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "create")) {
                log.debug("PaymentStatusRule success");
                return super.create(request);
            }
            return Mono.error(new CreateException(ext.getSpec().getDisplayName() + "暂未启用创建订单功能"));
        });
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "fetch")) {
                return super.fetch(request);
            }
            return Mono.error(new FetchException(ext.getSpec().getDisplayName() + "暂未启用查询订单功能"));
        });
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "cancel")) {
                return super.cancel(request);
            }
            return Mono.error(new CancelException(ext.getSpec().getDisplayName() + "暂未启用取消订单功能"));
        });
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "refund")) {
                return super.refund(request);
            }
            return Mono.error(new RefundException(ext.getSpec().getDisplayName() + "暂未启用退款功能"));
        });
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "refund")) {
                return super.fetchRefund(request);
            }
            return Mono.error(new RefundException(ext.getSpec().getDisplayName() + "暂未启用退款功能"));
        });
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "create")) {
                return super.paymentAsyncNotify(request);
            }
            return Mono.error(new BaseException(ext.getSpec().getDisplayName() + "暂未启用创建订单功能"));
        });
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "refund")) {
                return super.refundAsyncNotify(request);
            }
            return Mono.error(new BaseException(ext.getSpec().getDisplayName() + "暂未启用退款功能"));
        });
    }

    <T> Mono<T> enabled(Function<PaymentExtension, Mono<T>> fn) {
        return client.fetch(PaymentExtension.class, payment.getDescriptor().getName())
                .filter(e -> e.getSpec().getEnabled())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BaseException("插件暂无配置，请配置后重试"))))
                .flatMap(ext -> {
                    if (Boolean.TRUE.equals(ext.getSpec().getEnabled())) {
                        return Mono.just(ext);
                    }
                    return Mono.error(new BaseException(ext.getSpec().getDisplayName() + "插件已关闭"));
                })
                .flatMap(fn);
    }

    boolean contains(PaymentExtension ext, String method) {
        return null != ext && null != ext.getSpec().getEnableMethods() && ext.getSpec().getEnableMethods().contains(method);
    }

}
