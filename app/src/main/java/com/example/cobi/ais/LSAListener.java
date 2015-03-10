package com.example.cobi.ais;

import android.location.Location;

/**
 * Created by cobi on 20.02.15.
 */
public interface LSAListener {
    public abstract void onNewNearestLSA(LSA lsa, Location loc);
}
