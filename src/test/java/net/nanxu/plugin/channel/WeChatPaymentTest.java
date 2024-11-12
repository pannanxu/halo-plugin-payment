package net.nanxu.plugin.channel;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.nanxu.payment.account.Account;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import net.nanxu.payment.money.Money;
import net.nanxu.payment.order.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import run.halo.app.infra.utils.JsonUtils;

@ExtendWith(MockitoExtension.class)
class WeChatPaymentTest {
    WeChatPayment payment = new WeChatPayment();
    IAccount account = payment.createAccount(createWeChatAccount()).block();

    String orderNo = "order-no";

    @Test
    void pay() {
        PaymentRequest request = new PaymentRequest();
        request.setOrder(createOrder());
        request.setAccount(account);
        PaymentResult result = payment.pay(request).block();

        Assertions.assertNotNull(result);
        System.out.println(result.getContent());
        Assertions.assertTrue(result.getContent().startsWith("weixin://wxpay/bizpayurl?pr="));

    }

    @Test
    void query() {
        QueryResult result =
            payment.query(QueryRequest.builder().orderNo(orderNo).account(account).build()).block();
        System.out.println(result);
    }


    @Test
    void refund() {
        RefundResult result = payment.refund(RefundRequest.builder()
            .refundNo("refund-no")
            .orderNo(orderNo)
            .money(Money.of(new BigDecimal("0.01"), "CNY"))
            .total(Money.of(new BigDecimal("0.01"), "CNY"))
            .account(account).build()).block();
        System.out.println(result);
    }

    @Test
    void cancel() {
        RefundResult result =
            payment.cancel(RefundRequest.builder().orderNo(orderNo).account(account).build())
                .block();
        System.out.println(result);
    }

    Order createOrder() {
        return Order.createOrder()
            .setOrderNo("order-no")
            .setSubject("subject")
            .setMoney(Money.ofCNY(1L))
            .setAccount(new Order.AccountRef("simple"))
            .setChannel(Order.ChannelRef.of(WeChatPayment.NAME, WeChatPayment.NATIVE_METHOD)
                .setNotifyUrl("https://www.nanxu.net/payment"));
    }

    Account createWeChatAccount() {
        String config = null;
        try {
            config = Files.readString(Paths.get("data.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Account()
            .setName("simple")
            .setChannel(WeChatPayment.NAME)
            .setType("")
            .setConfig(JsonUtils.jsonToObject(config, ObjectNode.class));
    }
}