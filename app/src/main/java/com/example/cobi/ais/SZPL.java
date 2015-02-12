package com.example.cobi.ais;

import java.lang.reflect.Array;

/**
 * Created by cobi on 12.02.15.
 */
public class SZPL {
    private int duration;
    private int timeFrom;
    private int timeTo;
    private int greenFrom;
    private int greenTo;

    public SZPL(int duration, int timeFrom, int timeTo, int greenFrom, int greenTo) {
        this.duration = duration;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.greenFrom = greenFrom;
        this.greenTo = greenTo;
    }
}
