package com.example.cobi.ais;

/**
 * Created by cobi on 12.02.15.
 *
 * Die Klasse SZPL repräsentiert einen Signalschaltplan
 *
 * @param days      repräsentiert die geltenden Wochentage
 * @param timeFrom  repräsentiert die Anfangszeit des Plans
 * @param timeTo    repräsentiert die Endzeit des Plans
 * @param greenFrom repräsentiert den Start der Grünphase
 * @param greenTo   repräsentiert das Ende der Grünphase
 *
 */
public class SZPL {

    private int[] days;
    private int timeFrom;
    private int timeTo;
    private int greenFrom;
    private int greenTo;

    public SZPL(int[] days, int timeFrom, int timeTo, int greenFrom, int greenTo) {
        this.days = days;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.greenFrom = greenFrom;
        this.greenTo = greenTo;
    }

    public int[] getDays() {
        return days;
    }

    public int getTimeFrom() {
        return timeFrom;
    }

    public int getTimeTo() {
        return timeTo;
    }

    public int getGreenFrom() {
        return greenFrom;
    }

    public int getGreenTo() {
        return greenTo;
    }
}