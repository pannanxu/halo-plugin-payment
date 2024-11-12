package net.nanxu.payment.channel.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.nanxu.payment.account.Account;
import net.nanxu.payment.account.AccountService;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.account.IAccountRouter;
import net.nanxu.payment.account.PaymentAccount;
import net.nanxu.payment.channel.AbstractPayment;
import net.nanxu.payment.channel.IPaymentCallback;
import net.nanxu.payment.channel.IPaymentSupport;
import net.nanxu.payment.channel.PaymentRegistry;
import net.nanxu.payment.channel.model.CallbackRequest;
import net.nanxu.payment.channel.model.CallbackResult;
import net.nanxu.payment.channel.model.PayRequest;
import net.nanxu.payment.channel.model.PaymentProfile;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import net.nanxu.payment.channel.model.SettingField;
import net.nanxu.payment.infra.ProtocolPacket;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import net.nanxu.payment.security.SecurityRegistry;
import net.nanxu.payment.utils.QrCodeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {


    @Spy
    SecurityRegistry security = new SecurityRegistry();
    @Mock
    PaymentRegistry paymentRegistry;
    @Mock
    OrderService orderService;
    @Mock
    AccountService accountService;
    @Mock
    CacheManager cacheManager;
    @Mock
    IAccountRouter accountRouter;
    @InjectMocks
    PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        when(paymentRegistry.get("test_channel")).thenReturn(new TestChannel());
        when(orderService.getOrder(any())).thenReturn(Mono.just(createOrder()));
        when(accountService.getAccount("test_account")).thenReturn(
            Mono.just(new TestChannel.TestAccount(
                new Account().setName("test_account").setChannel("test_channel")
            )));
        when(cacheManager.getCache(any())).thenReturn(null);
    }

    @Test
    void pay() {
        PaymentRequest request = new PaymentRequest();
        request.setOrder(createOrder());
        PayRequest payRequest = new PayRequest();
        payRequest.setOrderNo("test");
        payRequest.setChannel("test_channel");
        payRequest.setPacket(new ProtocolPacket());
        PaymentResult pay = paymentService.pay(payRequest).block();
        assertNotNull(pay);
        assertTrue(pay.getType().equals(PaymentResult.Type.QRCode)
            && pay.getContent().startsWith(QrCodeUtil.BASE64_PREFIX));

    }

    // @Test
    // void query() {
    // }
    //
    // @Test
    // void refund() {
    // }
    //
    // @Test
    // void cancel() {
    // }

    Order createOrder() {
        return Order.createOrder()
            .setOrderNo("order-no")
            .setSubject("subject")
            .setAccount(new Order.AccountRef("test_account"))
            .setChannel(new Order.ChannelRef("test_channel"));
    }

    static class TestChannel extends AbstractPayment {
        public static final String NAME = "test_channel";

        protected TestChannel() {
            super(PaymentProfile.create(NAME, "TestChannel", "/test.png"),
                List.of(SettingField.text("appid", "APPID").required(),
                    SettingField.text("secret", "SECRET").required(),
                    SettingField.text("mchid", "MCHID").required()),
                new TestPaymentSupport(), new TestPaymentCallback());
        }

        @Override
        public Mono<IAccount> createAccount(IAccount account) {
            return null;
        }

        @Override
        public Mono<PaymentResult> pay(PaymentRequest request) {
            return Mono.just(PaymentResult.builder()
                .status(PaymentResult.Status.SUCCESS)
                .type(PaymentResult.Type.QRCode)
                .content("https://test.com")
                .expiresAt(Instant.now().plusSeconds(60))
                .order(request.getOrder())
                .build());
        }

        @Override
        public Mono<QueryResult> query(QueryRequest request) {
            return null;
        }

        @Override
        public Mono<RefundResult> refund(RefundRequest request) {
            return null;
        }

        @Override
        public Mono<RefundResult> cancel(RefundRequest request) {
            return null;
        }

        public static class TestPaymentSupport implements IPaymentSupport {
        }

        public static class TestPaymentCallback implements IPaymentCallback {
            @Override
            public Mono<CallbackResult> callback(CallbackRequest request) {
                return null;
            }
        }

        @Getter
        @EqualsAndHashCode(callSuper = true)
        public static class TestAccount extends PaymentAccount {

            public TestAccount(IAccount account) {
                super(account);
            }

        }

    }
}