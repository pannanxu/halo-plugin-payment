# halo-plugin-email

Payment plugin for Halo2.0

## 功能

- [x] SDK
- [x] Core Plugin
- [x] 微信支付 Plugin
  - [x] PC
  - [ ] 移动端网页
  - [ ] App
  - [ ] 小程序
  - [ ] 公众号
- [x] 支付宝支付 Plugin
  - [x] PC
  - [ ] 移动端网页
  - [ ] App
  - [ ] 小程序

## 使用

WIP

## 自定义支付

```java
@Component
public class WechatPayment extends AbstractPaymentOperator {
    // impl methods...
}
```


```java
@Component
public class WechatPaymentPlugin extends BasePlugin {
    // ....
    @Override
    public void start() {
        SdkContextHolder.register(this);
    }
    // ...
}
```

## 业务集成

WIP

## 构建生产产物

```
./gradlew -x build
```

然后只需复制例如`build/libs/halo-plugin-payment-*.jar` 的 `jar` 包即可使用。