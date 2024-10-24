package net.nanxu.payment.router;

import reactor.core.publisher.Mono;

/**
 * 智能路由, 由用户自定义规则匹配出指定的数据.
 * <p>
 * 通道示例:
 * <p>
 * 当 "HttpHeader" 的 User-Agent 包含 "alipayclient" 时，使用支付宝.
 * 当 "HttpHeader" 的 User-Agent 包含 "micromessenger" 时，使用微信.
 * 当 "DeviceType" 为 "pc" 时，使用的支付渠道列表(由用户配置)
 * 当 "DeviceType" 为 "wap" 时，使用的支付渠道列表(由用户配置)
 * 当 "DeviceType" 为 "app" 时，使用的支付渠道列表(由用户配置)
 * 当 "DeviceType" 为 "mini" 时，使用的支付渠道列表(由用户配置)
 * 当 "DeviceType" 为 "other" 时，使用的支付渠道列表(由用户配置)
 * <p>
 * 账户示例：
 * <p>
 * 当 “业务插件名称” 为 “Simple” 时，使用账户 “Simple” 权重+1
 * 当 “订单产品链接” 为 “/product/simple” 时，使用账户 “ProductSimple” 权重+1
 * 当 “账户通道费率” 大于 “0.01” 时，使用账户 “FeeSimple” 否则，使用账户 “Simple” 权重+1
 * 当 “订单金额” 小于 “100” 时，使用账户 “FeeSimple” 权重+1
 * <p>
 * 假设账户权重计算得出：Simple=1, ProductSimple=2, FeeSimple=3
 * 将使用权重值最高的一个账户FeeSimple
 *
 * @author: P
 **/
public interface IRouter<T> {

    Mono<T> filter(RouterFilterRequest request);

}
