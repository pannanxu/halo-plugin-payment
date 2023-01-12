package io.mvvm.halo.plugins.payment.sdk.exception;

/**
 * ExceptionCode.
 *
 * @author: pan
 **/
public enum ExceptionCode {
    /**
     * 插件已关闭
     */
    payment_closed,
    /**
     * 退款功能已关闭
     */
    refund_method_closed,
    /**
     * 下单功能已关闭
     */
    create_method_closed,
    /**
     * 关闭订单功能已关闭
     */
    cancel_method_closed,
    /**
     * 查询订单功能已关闭
     */
    fetch_method_closed,
    /**
     * 黑名单
     */
    black_list,
    /**
     * 限流
     */
    limit_request,
    /**
     * 业务处理异常，通常出现此错误直接弹出提示即可
     */
    biz_error,
    /**
     * 未知异常
     */
    error
}
