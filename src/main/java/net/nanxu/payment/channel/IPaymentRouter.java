package net.nanxu.payment.channel;

import java.util.List;
import net.nanxu.payment.router.IRouter;
import net.nanxu.payment.router.RouterFilterRequest;
import reactor.core.publisher.Mono;

/**
 * 支付通道路由器, 传递一个条件, 返回一组支付通道.
 *
 * @author: P
 **/
public interface IPaymentRouter extends IRouter<List<IPayment>> {

    Mono<List<IPayment>> filter(RouterFilterRequest request);

}
