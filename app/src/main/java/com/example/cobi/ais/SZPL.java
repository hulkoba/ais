package com.example.cobi.ais;

/**
 * Created by cobi on 12.02.15.
 */
public class SZPL {

    private String[] days;
    private int duration;
    private int timeFrom;
    private int timeTo;
    private int greenFrom;
    private int greenTo;

    public SZPL(String[] days, int duration, int timeFrom, int timeTo, int greenFrom, int greenTo) {
        this.days = days;
        this.duration = duration;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.greenFrom = greenFrom;
        this.greenTo = greenTo;
    }
}
