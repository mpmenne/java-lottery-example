package com.google.code.flabbyraffle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: 4/28/11
 * Time: 10:30 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/com/google/code/flabbyraffle/test-spring-config.xml")
public class Raffle_AT {

    private static final Integer JACKPOT_AMOUNT = 50;

    @Resource (name = "lotteryTicketMachine")
    private LotteryTicketMachine lotteryTicketMachine;
    @Resource (name = "fakeLotteryNumberGenerator")
    private FakeLotteryNumberGenerator fakeLotteryNumberGenerator;
    @Resource (name = "fakePowerballNumberGenerator")
    private FakeLotteryNumberGenerator fakePowerballNumberGenerator;

    @Before
    public void setup() {

    }

    /**
     * Normal Lottery Tests
     *
     * 1) Lottery numbers are seperated with spaces.
     * 2) Each ticket has a unique ticket number.
     * 3) Tickets are sepearted by a new line
     * i.e.   1000, 1 23 57  \\n 1001, 45 2 68
     *
     * Each matching lottery number .
     * For example,  (Winning Numbers) 1 2 3   (Your Numbers) 1 6 7     =>   1 X JACKPOT
     *               (Winning Numbers) 1 2 3   (Your Numbers) 1 1 7     =>   2 X JACKPOT
     *               (Winning Numbers) 1 2 3   (Your Numbers) 1 5 2     =>   2 X JACKPOT
     *               (Winning Numbers) 1 2 3   (Your Numbers) 3 2 1     =>   3 X JACKPOT
     *
     */

    @Test
    public void oneWinningNumberOnOneWinningTicketMeansOneJackpot() {
        //winning numbers ... 21  30  40
        lotteryTicketMachine.uploadTickets("1001, 21");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(JACKPOT_AMOUNT * 1));
    }

    @Test
    public void oneTicketMatchesAllThreeNumbersMeansThreeJackpots() {
        //winning numbers ... 21  30  40
        lotteryTicketMachine.uploadTickets("1001, 30 40 21");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(JACKPOT_AMOUNT * 3));
    }

    @Test
    public void oneTicketMatchesTwoNumbersMeansTwoJackpots() {
        //winning numbers ... 21  30  40
        lotteryTicketMachine.uploadTickets("1001, 21 40 80");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(JACKPOT_AMOUNT * 2));
    }

    @Test
    public void oneTicketMatchesOneWinningNumberTwiceMeansTwoJackpots() {
        //winning numbers ... 21  30  40
        lotteryTicketMachine.uploadTickets("1001, 21 21");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(JACKPOT_AMOUNT * 2));
    }

    @Test
    public void ticketTwoMatchesAndGetsOneJackpotMatchesButTicketOneGetsNada() {
        //winning numbers ... 21  30  40
        lotteryTicketMachine.uploadTickets("1001, 22 22 22 89 78 53 67 \\n 1002, 21");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1002), is(JACKPOT_AMOUNT * 1));
    }

    @Test
    public void twoDifferentTicketsWithOneWinningNumberMeansTwoSeparateJackpots() {
        //winning numbers ... 21  30  40
        lotteryTicketMachine.uploadTickets("1001, 21 \\n 1002, 21");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(JACKPOT_AMOUNT * 1));
        assertThat(lotteryTicketMachine.ticketWinningsFor(1002), is(JACKPOT_AMOUNT * 1));
    }

    @Test
    public void twoDifferentTicketsTwoDifferentNumbersMeanTwoSeparateJackpots() {
        //winning numbers ... 21  30  40
        lotteryTicketMachine.uploadTickets("1001, 21 \\n 1002, 30");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(JACKPOT_AMOUNT * 1));
        assertThat(lotteryTicketMachine.ticketWinningsFor(1002), is(JACKPOT_AMOUNT * 1));
    }

    @Test
    public void twoDifferentTicketsWithThreeOfTheSameNumberMeansThreeJackpotsAPiece() {
        //winning numbers ... 21  30  40
        lotteryTicketMachine.uploadTickets("1001, 21 21 21 \\n 1002, 21 21 21");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(JACKPOT_AMOUNT * 3));
        assertThat(lotteryTicketMachine.ticketWinningsFor(1002), is(JACKPOT_AMOUNT * 3));
    }


    /**
     * Powerball Tests
     *
     * Powerball numbers are denoted with an X
     *
     * Matching a powerball number doubles your entire winnings after the multipliers.
     * For example,  (Winning Numbers) 1 2 3 x4   (Your Numbers) 1 6 7 x4     =>   2 X JACKPOT
     *               (Winning Numbers) 1 2 3 x4   (Your Numbers) 1 1 7 x4     =>   4 X JACKPOT
     *               (Winning Numbers) 1 2 3 x4   (Your Numbers) 1 5 2 x4     =>   4 X JACKPOT
     *               (Winning Numbers) 1 2 3 x4   (Your Numbers) 3 2 1 x4     =>   8 X JACKPOT
     *
     */

    @Test
    public void noWinningNoNumbersMeansNoJackpot() {
        //winning numbers 21 32 48 x13
        lotteryTicketMachine.uploadTickets("1001, 1 1 1 x1");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(0));
    }

    @Test
    public void weCanUploadMultiplePowerballTicketSales() {
        //winning numbers 21 32 48 x13
        lotteryTicketMachine.uploadTickets("1001, 1 1 1 x1 \\n 1002, 21 1 1 x1");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(0));
        assertThat(lotteryTicketMachine.ticketWinningsFor(1002), is(1 * JACKPOT_AMOUNT));
    }

    @Test
    public void weCanUploadPowerballAndLotteryTicketSales() {
        //winning numbers 21 32 48 x13
        lotteryTicketMachine.uploadTickets("1001, 1 1 21 x1 \\n 1002, 21 1 1");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(1 * JACKPOT_AMOUNT));
        assertThat(lotteryTicketMachine.ticketWinningsFor(1002), is(1 * JACKPOT_AMOUNT));
    }

    @Test
    public void matchingOnlyThePowerballIsOneJackpot() {
        //winning numbers 21 32 48 x13
        lotteryTicketMachine.uploadTickets("1001, 1 1 1 x13");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(1 * JACKPOT_AMOUNT));
    }

    @Test
    public void matchingPowerballAndOneNumberDoublesJackpot() {
        //winning numbers 21 32 48 x13
        lotteryTicketMachine.uploadTickets("1001, 1 1 21 x13");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(2 * JACKPOT_AMOUNT));
    }

    @Test
    public void matchingPowerballAndTwoNumbersQuadruplesJackpot() {
        //winning numbers 21 32 48 x13
        lotteryTicketMachine.uploadTickets("1001, 1 32 21 x13");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(4 * JACKPOT_AMOUNT));
    }

    @Test
    public void matchingPowerballAndThreeNumbersEightTimesJackpot() {
        //winning numbers 21 32 48 x13
        lotteryTicketMachine.uploadTickets("1001, 32 48 21 x13");

        lotteryTicketMachine.performDrawingFor(JACKPOT_AMOUNT);

        assertThat(lotteryTicketMachine.ticketWinningsFor(1001), is(8 * JACKPOT_AMOUNT));
    }
}
