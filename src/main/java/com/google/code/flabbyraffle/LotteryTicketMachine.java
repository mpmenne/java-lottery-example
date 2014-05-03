package com.google.code.flabbyraffle;

import javax.swing.event.ListSelectionEvent;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: 4/28/11
 * Time: 10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class LotteryTicketMachine {

    private int jackpotAmount;
    private LotteryNumberGenerator lotteryNumberGenerator;
    private Map<Integer, List<String>> ticketMap;
    private List<Integer> winningNumbers;

    public LotteryTicketMachine() {

    }

    public void uploadTickets(String ticketBatch) {
        ticketMap = new HashMap<Integer, List<String>>();
        String[] tickets = ticketBatch.split("\\\\n");
        for (String ticket : tickets) {
            String[] ticketParts = ticket.split(",");
            String ticketId = ticketParts[0];
            String ticketNumberString = ticketParts[1];
            String[] ticketNumbers = ticketNumberString.trim().split("\\s");
            List<String> ticketNumberMap = new ArrayList<String>();
            for (String ticketNumber : ticketNumbers) {
                ticketNumberMap.add(ticketNumber);
            }
            ticketMap.put(Integer.parseInt(ticketId.trim()), ticketNumberMap);
        }
    }

    public void performDrawingFor(int jackpotAmount) {
        this.jackpotAmount = jackpotAmount;
        String winningString = lotteryNumberGenerator.generateWinningNumbers();
        String[] numbers = winningString.trim().split("\\s");
        Integer number1 = Integer.parseInt(numbers[0]);
        Integer number2 = Integer.parseInt(numbers[1]);
        Integer number3 = Integer.parseInt(numbers[2]);
        winningNumbers = asList(number1, number2, number3);
    }

    public Integer ticketWinningsFor(Integer ticketId) {
        int totalJackpotWon = 0;
        List<String> ticketNumbers = ticketMap.get(ticketId);
        for (String ticketNumber : ticketNumbers) {
            for (Integer winningTicketNumber : winningNumbers) {
                if (Integer.parseInt(ticketNumber.trim()) == winningTicketNumber) {
                    totalJackpotWon += jackpotAmount;
                    break;
                }
            }
        }
        return totalJackpotWon;
    }

    public void setLotteryNumberGenerator(LotteryNumberGenerator lotteryNumberGenerator) {
        this.lotteryNumberGenerator = lotteryNumberGenerator;
    }
}
