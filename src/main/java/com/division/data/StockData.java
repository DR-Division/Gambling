package com.division.data;

public class StockData {

    private final int initial; //초기 값
    private final int width; //변동 폭, 10일경우 -5~5
    private double current; //현재 주식값
    private boolean warning; //투자 유의 경고 (초기값의 5%이하)
    private double percent; //등락율

    public StockData(int initial, int width, int current, boolean warning) {
        this.initial = initial;
        this.width = width;
        this.current = current;
        this.warning = warning;
        this.percent = 0.0;
    }

    public int getInitial() {
        return initial;
    }

    public int getWidth() {
        return width;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public boolean isWarning() {
        return warning;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
