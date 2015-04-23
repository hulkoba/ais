package com.example.cobi.ais;

/**
 * Created by cobi on 13.03.15.
 * gibt die berechnete Empfehlungen an die MainActivity zur Displayanzeige
 */
public interface AdviceListener {

    public abstract void lsaIsTrafficDependent();

    public abstract void needToStop();

    public abstract void speedIsOk();

    public abstract void needToIncreaseSpeed(int countdown);

    public abstract void seriouslyNeedToIncreaseSpeed(int countdown);

    public abstract void needToDecreaseSpeed(int countdown);

    public abstract void seriouslyNeedToDecreaseSpeed(int countdown);
}

