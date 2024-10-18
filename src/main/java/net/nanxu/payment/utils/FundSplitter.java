package net.nanxu.payment.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FundSplitter {

    public static BigDecimal[] splitFund(BigDecimal totalAmount, int numRecipients) {
        if (numRecipients <= 0) {
            throw new IllegalArgumentException("Number of recipients must be greater than zero.");
        }

        BigDecimal[] amounts = new BigDecimal[numRecipients];
        BigDecimal perPersonAmount = totalAmount.divide(new BigDecimal(numRecipients), 2, RoundingMode.HALF_UP);
        BigDecimal remainingAmount = totalAmount;

        for (int i = 0; i < numRecipients - 1; i++) {
            amounts[i] = perPersonAmount;
            remainingAmount = remainingAmount.subtract(perPersonAmount);
        }

        // 最后一个人承担剩余的金额, 保留两位小数
        amounts[numRecipients - 1] = remainingAmount.setScale(2, RoundingMode.UP);

        return amounts;
    }

    public static void main(String[] args) {
        BigDecimal totalAmount = new BigDecimal("1000.111");
        int numRecipients = 4;

        BigDecimal[] splitAmounts = splitFund(totalAmount, numRecipients);
        
        for (int i = 0; i < splitAmounts.length; i++) {
            System.out.println("Recipient " + (i + 1) + ": " + splitAmounts[i]);
        }
    }
}