package net.nanxu.payment.channel.impl;

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
import net.nanxu.payment.account.PaymentAccount;
import net.nanxu.payment.channel.AbstractPayment;
import net.nanxu.payment.channel.IPaymentCallback;
import net.nanxu.payment.channel.IPaymentSupport;
import net.nanxu.payment.channel.PaymentRegistry;
import net.nanxu.payment.channel.model.CallbackRequest;
import net.nanxu.payment.channel.model.CallbackResult;
import net.nanxu.payment.channel.model.PaymentProfile;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.PaymentSupport;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import net.nanxu.payment.channel.model.SettingField;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import net.nanxu.payment.security.SecurityRegistry;
import net.nanxu.payment.utils.QrCodeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    @InjectMocks
    PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        when(paymentRegistry.get("test_channel")).thenReturn(new TestChannel());
        when(orderService.createOrder(any())).thenReturn(Mono.just(createOrder()));
        when(accountService.getAccount("test_account")).thenReturn(
            Mono.just(new TestChannel.TestAccount(
                new Account().setName("test_account").setChannel("test_channel")
            )));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void pay() {
        PaymentRequest request = new PaymentRequest();
        request.setOrder(createOrder());
        Mono<PaymentResult> pay = paymentService.pay(request);

        pay.subscribe(System.out::println);

        StepVerifier.create(pay)
            .assertNext(e -> {
                assertTrue(e.getType().equals(PaymentResult.Type.QRCode)
                    && e.getContent().startsWith(QrCodeUtil.BASE64_PREFIX));
            })
            .verifyComplete();
    }

    @Test
    void query() {
    }

    @Test
    void refund() {
    }

    @Test
    void cancel() {
    }

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
            @Override
            public Mono<Boolean> pay(PaymentSupport request) {
                return Mono.just(request.getOrder().getChannel().getName().equals(NAME));
            }

            @Override
            public Mono<Boolean> query(QueryRequest request) {
                return Mono.just(true);
            }

            @Override
            public Mono<Boolean> refund(RefundRequest request) {
                return Mono.just(true);

            }

            @Override
            public Mono<Boolean> cancel(RefundRequest request) {
                return Mono.just(true);
            }
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