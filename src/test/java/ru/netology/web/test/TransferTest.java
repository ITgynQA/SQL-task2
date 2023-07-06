package ru.netology.web.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {
    private static String token;

    @BeforeAll
    public static void setToken() {
        token = DataHelper.getToken();
    }

    @SneakyThrows
    @AfterAll
    public static void cleanDB() {
        DataHelper.cleanDataBase();
    }


    @Test
    public void successfulTransferTest() {
        int initialBalance1 = DataHelper.gettingBalanceCard1(token, 200, 10000);
        int initialBalance2 = DataHelper.gettingBalanceCard2(token, 200, 10000);

        var transferInfo = DataHelper.getTransferInfo(DataHelper.getCardNumber1(), DataHelper.getCardNumber2(), 5000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 200);

        assertEquals(initialBalance1 - 5000, DataHelper.gettingBalanceCard1(token, 200, 5000));
        assertEquals(initialBalance2 + 5000, DataHelper.gettingBalanceCard2(token, 200, 15000));
    }

    @Test
    public void successfulTransferTestEqualsBalance() {
        int initialBalance1 = DataHelper.gettingBalanceCard1(token, 200, 5000);
        int initialBalance2 = DataHelper.gettingBalanceCard2(token, 200, 15000);

        var transferInfo = DataHelper.getTransferInfo(DataHelper.getCardNumber2(), DataHelper.getCardNumber1(), 10000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 200);

        assertEquals(initialBalance1 + 10000, DataHelper.gettingBalanceCard1(token, 200, 15000));
        assertEquals(initialBalance2 - 10000, DataHelper.gettingBalanceCard2(token, 200, 5000));
    }

    @Test
    public void negativeAmountTransferTest() {
        int initialBalance1 = DataHelper.gettingBalanceCard1(token, 200, 15000);
        int initialBalance2 = DataHelper.gettingBalanceCard2(token, 200, 5000);

        var transferInfo = DataHelper.getTransferInfo(DataHelper.getCardNumber1(), DataHelper.getCardNumber2(), -5000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 400);

        assertEquals(initialBalance1, DataHelper.gettingBalanceCard1(token, 400, 15000));
        assertEquals(initialBalance2, DataHelper.gettingBalanceCard2(token, 400, 5000));
    }

    @Test
    public void transferMoreBalanceTest() {
        int initialBalance1 = DataHelper.gettingBalanceCard1(token, 200, 15000);
        int initialBalance2 = DataHelper.gettingBalanceCard2(token, 200, 5000);

        var transferInfo = DataHelper.getTransferInfo(DataHelper.getCardNumber1(), DataHelper.getCardNumber2(), 11000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 400);

        assertEquals(initialBalance1, DataHelper.gettingBalanceCard1(token, 400, 15000));
        assertEquals(initialBalance2, DataHelper.gettingBalanceCard2(token, 400, 5000));
    }

    @Test
    public void unsuccessfulRandomTransferTest() {
        int initialBalance1 = DataHelper.getCard1Balance().getBalance();
        int initialRandomBalance = DataHelper.getCardRandomBalance().getBalance();

        var transferInfo = DataHelper.getTransferInfo(DataHelper.getCardNumber1(), DataHelper.getRandomCardNumber(), 5000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 400);

        assertEquals(initialBalance1, DataHelper.getCard1Balance().getBalance());
        assertEquals(initialRandomBalance, DataHelper.getCardRandomBalance().getBalance());
    }


}
