package com.example.cobi.ais;

import android.location.Location;

/**
 * Created by cobi on 20.02.15.
 */
public interface OnSetListener {
  //  public abstract void onSzplSet(SZPL szpl);
    public abstract void onLSASet(LSA lsa, Location loc);
}
