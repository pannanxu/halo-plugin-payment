package io.mvvm.halo.plugins.payment.code;

import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.simple.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.simple.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.simple.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.simple.PaymentInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Ref;

import java.util.Date;

/**
 * code 兑换码支付.
 *
 * @author: pan
 **/
@Component
public class CodePaymentOperator implements IPaymentOperator {

    private final ReactiveExtensionClient client;

    public CodePaymentOperator(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Ref type() {
        return Ref.of("code");
    }

    @Override
    public Mono<CreatePaymentResponse> create(CreatePaymentRequest request) {
        String code = request.getExpand().get("code").toString();
        return client.fetch(CodeExtension.class, code)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无此兑换码"))))
                .flatMap(ext -> {
                    if (CodeExtension.Status.disable.name().equals(ext.getSpec().getStatus())) {
                        return Mono.error(new RuntimeException("兑换码已禁用"));
                    }
                    if (CodeExtension.Status.used.name().equals(ext.getSpec().getStatus())) {
                        return Mono.error(new RuntimeException("兑换码已使用"));
                    }
                    ext.getSpec().setStatus(CodeExtension.Status.used.name());
                    ext.getSpec().setUsedTime(new Date());
                    ext.getSpec().setOutTradeNo(request.getOutTradeNo());
                    return Mono.just(ext);
                })
                .flatMap(client::update)
                .map(ext -> new CreatePaymentResponse()
                        .setOutTradeNo(ext.getSpec().getOutTradeNo())
                        .setStatus(PaymentStatus.successful)
                        .setSuccess(CodeExtension.Status.used.name().equals(ext.getSpec().getStatus()))
                        .setTotalFee(request.getTotalFee()));
    }

    @Override
    public Mono<PaymentInfo> fetch(PaymentRequest request) {
        return client.fetch(CodeExtension.class, request.getExpand().get("code").toString())
                .flatMap(ext -> {
                    CodeExtension.Spec spec = ext.getSpec();
                    PaymentInfo paymentInfo = new PaymentInfo();
                    if (CodeExtension.Status.used.name().equals(spec.getStatus())) {
                        paymentInfo.setStatus(PaymentStatus.successful);
                    } else {
                        paymentInfo.setStatus(PaymentStatus.created);
                    }
                    return Mono.just(paymentInfo.setOutTradeNo(spec.getOutTradeNo()));
                });
    }

    @Override
    public void destroy() {

    }

    @Override
    public Mono<AsyncNotifyResponse> asyncNotify(ServerRequest request) {
        return Mono.empty();
    }
}
