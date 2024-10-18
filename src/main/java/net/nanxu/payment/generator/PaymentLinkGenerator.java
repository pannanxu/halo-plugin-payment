package net.nanxu.payment.generator;

/**
 * 外部地址生成器.
 *
 * @author: P
 **/
public interface PaymentLinkGenerator {
    /**
     * 生成一个收银台访问地址
     *
     * @param orderNo 订单号
     * @param channel 支付渠道
     * @return 收银台地址
     */
    String checkoutUrl(String orderNo, String channel);

    /**
     * 生成一个支付状态回调接口地址
     *
     * @param internal 内部地址，由用户自定义的路径
     * @param orderNo 订单号
     * @param channel 支付渠道
     * @return 回调接口地址
     */
    String callbackUrl(String internal, String orderNo, String channel);
}
