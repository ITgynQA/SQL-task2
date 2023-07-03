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
    public void comparisonOfBalanceFromDataBaseAndApi() {
        int initialBalance1FromDB = DataHelper.getCard1Balance().getBalance();
        int initialBalance2FromDB = DataHelper.getCard2Balance().getBalance();

        int initialBalance1FromApi = DataHelper.cardsRequest1(token, 200, 10000);
        int initialBalance2FromApi = DataHelper.cardsRequest2(token, 200, 10000);

        assertEquals(initialBalance1FromDB, initialBalance1FromApi);
        assertEquals(initialBalance2FromDB, initialBalance2FromApi);
    }

    @Test
    public void successfulTransferTest() {
        int initialBalance1 = DataHelper.cardsRequest1(token, 200, 10000);
        int initialBalance2 = DataHelper.cardsRequest2(token, 200, 10000);

        var transferInfo = DataHelper.getTransfer1Info(5000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 200);

        assertEquals(initialBalance1 - 5000, DataHelper.cardsRequest1(token, 200, 5000));
        assertEquals(initialBalance2 + 5000, DataHelper.cardsRequest2(token, 200, 15000));
    }

    @Test
    public void successfulTransferTestEqualsBalance() {
        int initialBalance2 = DataHelper.cardsRequest1(token, 200, 10000);
        int initialBalance1 = DataHelper.cardsRequest2(token, 200, 10000);

        var transferInfo = DataHelper.getTransfer1Info(10000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 200);

        assertEquals(initialBalance1 - 10000, DataHelper.cardsRequest2(token, 200, 0));
        assertEquals(initialBalance2 + 10000, DataHelper.cardsRequest1(token, 200, 20000));
    }

    @Test
    public void negativeAmountTransferTest() {
        int initialBalance2 = DataHelper.cardsRequest1(token, 200, 10000);
        int initialBalance1 = DataHelper.cardsRequest2(token, 200, 10000);

        var transferInfo = DataHelper.getTransfer1Info(-5000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 400);

        assertEquals(initialBalance1, DataHelper.cardsRequest2(token, 400, 10000));
        assertEquals(initialBalance2, DataHelper.cardsRequest1(token, 400, 10000));
    }

    @Test
    public void transferMoreBalanceTest() {
        int initialBalance2 = DataHelper.cardsRequest1(token, 200, 10000);
        int initialBalance1 = DataHelper.cardsRequest2(token, 200, 10000);

        var transferInfo = DataHelper.getTransfer1Info(11000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 400);

        assertEquals(initialBalance1, DataHelper.cardsRequest2(token, 400, 10000));
        assertEquals(initialBalance2, DataHelper.cardsRequest1(token, 400, 10000));
    }

    @Test
    public void unsuccessfulRandomTransferTest() {
        int initialBalance1 = DataHelper.getCard1Balance().getBalance();
        int initialRandomBalance = DataHelper.getCardRandomBalance().getBalance();

        var transferInfo = DataHelper.getRandomTransferInfo(5000);

        DataHelper.transferRequest(token, transferInfo, "/api/transfer", 400);

        assertEquals(initialBalance1, DataHelper.getCard1Balance().getBalance());
        assertEquals(initialRandomBalance, DataHelper.getCardRandomBalance().getBalance());
    }


}
