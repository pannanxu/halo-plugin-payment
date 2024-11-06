package net.nanxu.payment.channel;

import net.nanxu.payment.channel.model.PaymentSupport;
import reactor.core.publisher.Mono;

/**
 * 第三方支付插件通过上下文验证是否能够提供功能的支持.
 *
 * @author: P
 **/
public interface IPaymentSupport {
    IPaymentSupport WAP = new IPaymentSupport() {
        @Override
        public Mono<Boolean> isSupported(PaymentSupport request) {
            return Mono.just(request.getPacket().isWap());
        }
    };
    
    IPaymentSupport PC = new IPaymentSupport() {
        @Override
        public Mono<Boolean> isSupported(PaymentSupport request) {
            return Mono.just(request.getPacket().isPc());
        }
    };
    
    IPaymentSupport App = new IPaymentSupport() {
        @Override
        public Mono<Boolean> isSupported(PaymentSupport request) {
            return Mono.just(request.getPacket().isApp());
        }
    };
    

    default Mono<Boolean> isSupported(PaymentSupport request) {
        return Mono.just(Boolean.FALSE);
    }
}
