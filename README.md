# Halo 聚合支付插件

只需要调用接口创建订单，无需关心支付逻辑



# 使用逻辑

## 角色

- 聚合支付插件：本插件
- 业务插件：其他任意业务需要支付的插件（创建订单的插件）
- 支付通道：为聚合支付插件提供支付通道的插件（实现IPayment接口的插件）
- 下单用户：购买商品的用户

```java
@Component
public class StarterPlugin extends BasePlugin {

    // ...
    
    @Override
    public void start() {
        // 1. 注册支付通道
        Payment.register(IPayment payment);
        // 2. 注册业务回调通知
        Payment.register(INotification notification);

        System.out.println("插件启动成功！");
    }

    @Override
    public void stop() {
        // 1. 卸载支付通道
        Payment.unregister(IPayment payment);
        // 2. 卸载业务回调通知
        Payment.unregister(INotification notification);

        System.out.println("插件停止！");
    }
}

// 3. 用户选择在页面点击立即购买后，通过业务插件接口创建订单，然后跳转到收银台页面（/payment/{orderNo}）
Payment.createOrder(Order order);
```