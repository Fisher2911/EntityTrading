package me.masterofthefish.entitytrading.trades;

import java.util.Random;

public class TradePoints {

    private final int minPointsGiven;
    private final int maxPointsGiven;

    public TradePoints(final int minPointsGiven, final int maxPointsGiven) {
        this.minPointsGiven = minPointsGiven;
        this.maxPointsGiven = maxPointsGiven;
    }

    public int getMinPointsGiven() {
        return minPointsGiven;
    }

    public int getMaxPointsGiven() {
        return maxPointsGiven;
    }

    public int getRandPoints() {
        return new Random().nextInt(maxPointsGiven - minPointsGiven) + minPointsGiven;
    }
}
