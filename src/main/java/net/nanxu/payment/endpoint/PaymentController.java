package net.nanxu.payment.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.channel.CallbackService;
import net.nanxu.payment.channel.PaymentService;
import net.nanxu.payment.channel.model.CallbackRequest;
import net.nanxu.payment.channel.model.PayRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.infra.ProtocolPacket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * PaymentController.
 *
 * @author: pan
 **/
// @ApiVersion("payment.nanxu.net/v1alpha1")
@RequestMapping("/payment")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final CallbackService callbackService;

    @GetMapping("/buy/{orderNo}")
    public Mono<String> buy(@PathVariable String orderNo) {
        // TODO 跳转收银台界面
        return Mono.just("buy.html");
    }

    /**
     * 获取通道支付参数
     *
     * @param orderNo 订单号
     * @param channel 支付通道
     */
    @PostMapping("/{orderNo}/pay/{channel}")
    public Mono<PaymentResult> pay(@PathVariable String orderNo, @PathVariable String channel,
        ServerRequest request) {
        PayRequest payRequest = new PayRequest();
        payRequest.setOrderNo(orderNo);
        payRequest.setChannel(channel);
        payRequest.setPacket(new ProtocolPacket());
        return paymentService.pay(payRequest);
    }

    /**
     * 支付回调接口
     *
     * @param internal 内部地址
     * @param channel 支付通道
     * @param orderNo 订单号
     * @param body 回调数据
     */
    @PostMapping("/{internal}/{orderNo}/callback/{channel}")
    public Mono<Object> callback(
        @PathVariable String internal,
        @PathVariable String channel,
        @PathVariable String orderNo,
        @RequestBody String body) {
        return callbackService.validateInternal(internal)
            .flatMap(e -> {
                if (!e) {
                    // 随便返回一个
                    return Mono.just("success");
                }
                return callbackService.callback(
                    CallbackRequest.builder().channel(channel).orderNo(orderNo).requestBody(body)
                        .build());
            });
    }

}
