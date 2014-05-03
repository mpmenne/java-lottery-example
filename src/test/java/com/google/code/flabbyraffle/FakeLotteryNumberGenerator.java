package com.google.code.flabbyraffle;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: 4/29/11
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class FakeLotteryNumberGenerator implements LotteryNumberGenerator {

    private String winningNumbers;

    public FakeLotteryNumberGenerator(String winningNumbers) {
        this.winningNumbers = winningNumbers;
    }

    public String generateWinningNumbers() {
        return winningNumbers;
    }
}
