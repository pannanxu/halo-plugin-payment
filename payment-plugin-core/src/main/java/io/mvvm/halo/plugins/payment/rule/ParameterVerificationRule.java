package io.mvvm.halo.plugins.payment.rule;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.exception.CancelException;
import io.mvvm.halo.plugins.payment.sdk.exception.CreateException;
import io.mvvm.halo.plugins.payment.sdk.exception.ExceptionCode;
import io.mvvm.halo.plugins.payment.sdk.exception.FetchException;
import io.mvvm.halo.plugins.payment.sdk.exception.RefundException;
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
 * 参数校验规则.
 *
 * @author: pan
 **/
@Slf4j
public class ParameterVerificationRule extends BasePaymentRule {
    public ParameterVerificationRule(IPayment payment) {
        super(payment);
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        if (!StringUtils.hasLength(request.getOutTradeNo())) {
            return Mono.error(new CreateException(ExceptionCode.parameter_error, "单号不能为空"));
        }
        if (!StringUtils.hasLength(request.getTitle())) {
            return Mono.error(new CreateException(ExceptionCode.parameter_error, "标题不能为空"));
        }
        if (!StringUtils.hasLength(request.getCreator().getIpAddress())) {
            return Mono.error(new CreateException(ExceptionCode.parameter_error, "客户端IP不能为空"));
        }
        if (!StringUtils.hasLength(request.getCreator().getDevice())) {
            return Mono.error(new CreateException(ExceptionCode.parameter_error, "设备类型不能为空"));
        }
        if (null == request.getMoney() || null == request.getMoney().getTotal()) {
            return Mono.error(new CreateException(ExceptionCode.parameter_error, "金额不能为空"));
        }
        if (!StringUtils.hasLength(request.getBiz().getGvk())) {
            return Mono.error(new CreateException(ExceptionCode.parameter_error, "业务引用标识不能为空"));
        }
        return super.create(request);
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        if (!StringUtils.hasLength(request.getOutTradeNo())) {
            return Mono.error(new FetchException(ExceptionCode.parameter_error, "单号不能为空"));
        }
        return super.fetch(request);
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        if (!StringUtils.hasLength(request.getOutTradeNo())) {
            return Mono.error(new CancelException(ExceptionCode.parameter_error, "单号不能为空"));
        }
        return super.cancel(request);
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request) {
        if (!StringUtils.hasLength(request.getOutTradeNo())) {
            return Mono.error(new RefundException(ExceptionCode.parameter_error, "单号不能为空"));
        }
        if (!StringUtils.hasLength(request.getRefundNo())) {
            return Mono.error(new RefundException(ExceptionCode.parameter_error, "退款批次号不能为空"));
        }
        if (null == request.getMoney() || null == request.getMoney().getTotal()) {
            return Mono.error(new RefundException(ExceptionCode.parameter_error, "订单金额不能为空"));
        }
        if (null == request.getRefundMoney() || null == request.getRefundMoney().getTotal()) {
            return Mono.error(new RefundException(ExceptionCode.parameter_error, "退款金额不能为空"));
        }
        if (request.getRefundMoney().getTotal() > request.getRefundMoney().getTotal()) {
            return Mono.error(new RefundException(ExceptionCode.parameter_error, "退款金额不能大于订单总金额"));
        }
        if (!StringUtils.hasLength(request.getGvk())) {
            return Mono.error(new RefundException(ExceptionCode.parameter_error, "业务引用标识不能为空"));
        }
        return super.refund(request);
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request) {
        if (!StringUtils.hasLength(request.getOutTradeNo())) {
            return Mono.error(new FetchException(ExceptionCode.parameter_error, "单号不能为空"));
        }
        if (!StringUtils.hasLength(request.getRefundNo())) {
            return Mono.error(new FetchException(ExceptionCode.parameter_error, "退款批次号不能为空"));
        }
        return super.fetchRefund(request);
    }

}
