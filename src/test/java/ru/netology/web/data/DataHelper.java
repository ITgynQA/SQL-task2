package ru.netology.web.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DataHelper {
    public static QueryRunner runner = new QueryRunner();

    private DataHelper() {

    }

    @SneakyThrows
    public static Connection getConn() {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost:9999")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void transferRequest(String token, DataHelper.TransferInfo transferInfo, String path, int statusCode) {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(transferInfo)
                .post(path)
                .then()
                .statusCode(statusCode);
    }

    private static String token =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY";

    public static String getToken() {
        return token;
    }

    @SneakyThrows
    public static void cleanDataBase() {
        var connection = getConn();
        runner.execute(connection, "DELETE FROM card_transactions");
        runner.execute(connection, "DELETE FROM cards");
        runner.execute(connection, "DELETE FROM auth_codes");
        runner.execute(connection, "DELETE FROM users");
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    @SneakyThrows
    public static VerificationCode getVerificationCode() {
        var authCode = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1";
        var conn = DataHelper.getConn();
        var code = runner.query(conn, authCode, new ScalarHandler<String>());
        return new VerificationCode(code);
    }

    @Value
    public static class CardNumber {
        String cardNumber;
    }

    public static CardNumber getCardNumber1() {
        return new CardNumber("5559 0000 0000 0001");
    }

    public static CardNumber getCardNumber2() {
        return new CardNumber("5559 0000 0000 0002");
    }

    public static CardNumber getRandomCardNumber() {
        return new CardNumber("5559 0000 0000 0008");
    }

    @Value
    public static class TransferInfo {
        String from;
        String to;
        int amount;
    }
    public static TransferInfo getTransferInfo(CardNumber from, CardNumber to, int amount) {

        return new TransferInfo(from.getCardNumber(), to.getCardNumber(), amount);
    }

    @Value
    public static class CardBalance {
        int balance;
    }

    @SneakyThrows
    public static CardBalance getCard1Balance() {
        var conn = getConn();
        var balance = ("SELECT balance_in_kopecks FROM cards WHERE number = '5559 0000 0000 0001'");
        var balance1 = runner.query(conn, balance, new ScalarHandler<Integer>());
        return new CardBalance(balance1);
    }

    @SneakyThrows
    public static CardBalance getCard2Balance() {
        var conn = getConn();
        var balance = ("SELECT balance_in_kopecks FROM cards WHERE number = '5559 0000 0000 0002'");
        var balance2 = runner.query(conn, balance, new ScalarHandler<Integer>());
        return new CardBalance(balance2);
    }

    @SneakyThrows
    public static CardBalance getCardRandomBalance() {
        var randomBalance = (20000);
        return new CardBalance(randomBalance);
    }

    public static int gettingBalanceCard1(String token, int statusCode, int balance) {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/cards")
                .then()
                .statusCode(statusCode)
                .body("[1].balance", equalTo(balance));

        return balance;
    }

    public static int gettingBalanceCard2(String token, int statusCode, int balance) {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/cards")
                .then()
                .statusCode(statusCode)
                .body("[0].balance", equalTo(balance));

        return balance;
    }


}





