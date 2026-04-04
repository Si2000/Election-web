package com.election.backendjava.model;

public class MetaData {
    private int cast;
    private int totalCounted;
    private int rejectedOngeldig;
    private int rejectedBlanco;
    private int uncountedGeldigeVolmachtbewijzen;
    private int uncountedMeerGeteldeStembiljetten;
    private int uncountedMinderGeteldeStembiljetten;
    private int uncountedMeegenomenStembiljetten;
    private int uncountedTeWeinigUitgereikteStembiljetten;
    private int uncountedTeVeelUitgereikteStembiljetten;
    private int uncountedGeenVerklaring;
    private int uncountedAndereVerklaring;


    public MetaData( int cast, int totalCounted, int rejectedOngeldig, int rejectedBlanco, int uncountedGeldigeVolmachtbewijzen, int uncountedMeerGeteldeStembiljetten, int uncountedMinderGeteldeStembiljetten, int uncountedMeegenomenStembiljetten, int uncountedTeWeinigUitgereikteStembiljetten, int uncountedTeVeelUitgereikteStembiljetten, int uncountedGeenVerklaring, int uncountedAndereVerklaring) {
        this.cast = cast;
        this.totalCounted = totalCounted;
        this.rejectedOngeldig = rejectedOngeldig;
        this.rejectedBlanco = rejectedBlanco;
        this.uncountedGeldigeVolmachtbewijzen = uncountedGeldigeVolmachtbewijzen;
        this.uncountedMeerGeteldeStembiljetten = uncountedMeerGeteldeStembiljetten;
        this.uncountedMinderGeteldeStembiljetten = uncountedMinderGeteldeStembiljetten;
        this.uncountedMeegenomenStembiljetten = uncountedMeegenomenStembiljetten;
        this.uncountedTeWeinigUitgereikteStembiljetten = uncountedTeWeinigUitgereikteStembiljetten;
        this.uncountedTeVeelUitgereikteStembiljetten = uncountedTeVeelUitgereikteStembiljetten;
        this.uncountedGeenVerklaring = uncountedGeenVerklaring;
        this.uncountedAndereVerklaring = uncountedAndereVerklaring;
    }


    public int getCast() {
        return cast;
    }

    public int getTotalCounted() {
        return totalCounted;
    }

    public int getRejectedOngeldig() {
        return rejectedOngeldig;
    }

    public int getRejectedBlanco() {
        return rejectedBlanco;
    }

    public int getUncountedGeldigeVolmachtbewijzen() {
        return uncountedGeldigeVolmachtbewijzen;
    }

    public int getUncountedMeerGeteldeStembiljetten() {
        return uncountedMeerGeteldeStembiljetten;
    }

    public int getUncountedMinderGeteldeStembiljetten() {
        return uncountedMinderGeteldeStembiljetten;
    }

    public int getUncountedMeegenomenStembiljetten() {
        return uncountedMeegenomenStembiljetten;
    }

    public int getUncountedTeWeinigUitgereikteStembiljetten() {
        return uncountedTeWeinigUitgereikteStembiljetten;
    }

    public int getUncountedTeVeelUitgereikteStembiljetten() {
        return uncountedTeVeelUitgereikteStembiljetten;
    }

    public int getUncountedGeenVerklaring() {
        return uncountedGeenVerklaring;
    }

    public int getUncountedAndereVerklaring() {
        return uncountedAndereVerklaring;
    }
}
