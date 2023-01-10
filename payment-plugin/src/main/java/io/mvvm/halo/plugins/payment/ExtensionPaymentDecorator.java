package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentExtension;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

import java.util.function.Function;

/**
 * ExtensionPaymentDecorator.
 *
 * @author: pan
 **/
public class ExtensionPaymentDecorator implements IPayment {

    private final IPayment payment;
    private final ReactiveExtensionClient client;

    public ExtensionPaymentDecorator(IPayment payment, ReactiveExtensionClient client) {
        this.payment = payment;
        this.client = client;
    }

    @Override
    public PaymentDescriptor getDescriptor() {
        return payment.getDescriptor();
    }

    @Override
    public Mono<Boolean> status() {
        return payment.status()
                .flatMap(status -> {
                    if (status) {
                        return client.fetch(PaymentExtension.class, payment.getDescriptor().getName())
                                .map(PaymentExtension::getEnabled)
                                .switchIfEmpty(Mono.just(false));
                    }
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "create")) {
                return payment.create(request);
            }
            return Mono.error(new BaseException(ext.getDisplayName() + "暂未启用创建订单功能"));
        });
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "fetch")) {
                return payment.fetch(request);
            }
            return Mono.error(new BaseException(ext.getDisplayName() + "暂未启用查询订单功能"));
        });
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "cancel")) {
                return payment.cancel(request);
            }
            return Mono.error(new BaseException(ext.getDisplayName() + "暂未启用取消订单功能"));
        });
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "refund")) {
                return payment.refund(request);
            }
            return Mono.error(new BaseException(ext.getDisplayName() + "暂未启用退款功能"));
        });
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "refund")) {
                return payment.fetchRefund(request);
            }
            return Mono.error(new BaseException(ext.getDisplayName() + "暂未启用退款功能"));
        });
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "create")) {
                return payment.paymentAsyncNotify(request);
            }
            return Mono.error(new BaseException(ext.getDisplayName() + "暂未启用创建订单功能"));
        });
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        return enabled((ext) -> {
            if (contains(ext, "refund")) {
                return payment.refundAsyncNotify(request);
            }
            return Mono.error(new BaseException(ext.getDisplayName() + "暂未启用退款功能"));
        });
    }

    <T> Mono<T> enabled(Function<PaymentExtension, Mono<T>> fn) {
        return client.fetch(PaymentExtension.class, payment.getDescriptor().getName())
                .filter(PaymentExtension::getEnabled)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BaseException("插件暂无配置，请配置后重试"))))
                .flatMap(ext -> {
                    if (Boolean.TRUE.equals(ext.getEnabled())) {
                        return Mono.just(ext);
                    }
                    return Mono.error(new BaseException(ext.getDisplayName() + "插件已关闭"));
                })
                .flatMap(fn);
    }

    boolean contains(PaymentExtension ext, String method) {
        return null != ext && null != ext.getEnableMethods() && ext.getEnableMethods().contains(method);
    }
}
