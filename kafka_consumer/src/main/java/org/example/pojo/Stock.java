package org.example.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String sym;
    private String mc;
    private double c;
    private double f;
    private double r;
    private double lastPrice;
    private double lastVolume;
    private double lot;
    private String ot;
    private String changePc;
    private String avePrice;
    private String highPrice;
    private String lowPrice;
    private String fBVol;
    private String fBValue;
    private String fSVolume;
    private String fSValue;
    private String fRoom;
    private String g1;
    private String g2;
    private String g3;
    private String g4;
    private String g5;
    private String g6;
    private String g7;
    private String mp;
    private String CWUnderlying;
    private String CWIssuerName;
    private String CWType;
    private String CWMaturityDate;
    private String CWLastTradingDate;
    private String CWExcersisePrice;
    private String CWExerciseRatio;
    private String CWListedShare;

    private String sType;
    private String sBenefit;
    private String industry;
    private Timestamp crawledTime;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSym() {
        return sym;
    }

    public void setSym(String sym) {
        this.sym = sym;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getLastVolume() {
        return lastVolume;
    }

    public void setLastVolume(double lastVolume) {
        this.lastVolume = lastVolume;
    }

    public double getLot() {
        return lot;
    }

    public void setLot(double lot) {
        this.lot = lot;
    }

    public String getOt() {
        return ot;
    }

    public void setOt(String ot) {
        this.ot = ot;
    }

    public String getChangePc() {
        return changePc;
    }

    public void setChangePc(String changePc) {
        this.changePc = changePc;
    }

    public String getAvePrice() {
        return avePrice;
    }

    public void setAvePrice(String avePrice) {
        this.avePrice = avePrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getfBVol() {
        return fBVol;
    }

    public void setfBVol(String fBVol) {
        this.fBVol = fBVol;
    }

    public String getfBValue() {
        return fBValue;
    }

    public void setfBValue(String fBValue) {
        this.fBValue = fBValue;
    }

    public String getfSVolume() {
        return fSVolume;
    }

    public void setfSVolume(String fSVolume) {
        this.fSVolume = fSVolume;
    }

    public String getfSValue() {
        return fSValue;
    }

    public void setfSValue(String fSValue) {
        this.fSValue = fSValue;
    }

    public String getfRoom() {
        return fRoom;
    }

    public void setfRoom(String fRoom) {
        this.fRoom = fRoom;
    }

    public String getG1() {
        return g1;
    }

    public void setG1(String g1) {
        this.g1 = g1;
    }

    public String getG2() {
        return g2;
    }

    public void setG2(String g2) {
        this.g2 = g2;
    }

    public String getG3() {
        return g3;
    }

    public void setG3(String g3) {
        this.g3 = g3;
    }

    public String getG4() {
        return g4;
    }

    public void setG4(String g4) {
        this.g4 = g4;
    }

    public String getG5() {
        return g5;
    }

    public void setG5(String g5) {
        this.g5 = g5;
    }

    public String getG6() {
        return g6;
    }

    public void setG6(String g6) {
        this.g6 = g6;
    }

    public String getG7() {
        return g7;
    }

    public void setG7(String g7) {
        this.g7 = g7;
    }

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp;
    }

    public String getCWUnderlying() {
        return CWUnderlying;
    }

    public void setCWUnderlying(String CWUnderlying) {
        this.CWUnderlying = CWUnderlying;
    }

    public String getCWIssuerName() {
        return CWIssuerName;
    }

    public void setCWIssuerName(String CWIssuerName) {
        this.CWIssuerName = CWIssuerName;
    }

    public String getCWType() {
        return CWType;
    }

    public void setCWType(String CWType) {
        this.CWType = CWType;
    }

    public String getCWMaturityDate() {
        return CWMaturityDate;
    }

    public void setCWMaturityDate(String CWMaturityDate) {
        this.CWMaturityDate = CWMaturityDate;
    }

    public String getCWLastTradingDate() {
        return CWLastTradingDate;
    }

    public void setCWLastTradingDate(String CWLastTradingDate) {
        this.CWLastTradingDate = CWLastTradingDate;
    }

    public String getCWExcersisePrice() {
        return CWExcersisePrice;
    }

    public void setCWExcersisePrice(String CWExcersisePrice) {
        this.CWExcersisePrice = CWExcersisePrice;
    }

    public String getCWExerciseRatio() {
        return CWExerciseRatio;
    }

    public void setCWExerciseRatio(String CWExerciseRatio) {
        this.CWExerciseRatio = CWExerciseRatio;
    }

    public String getCWListedShare() {
        return CWListedShare;
    }

    public void setCWListedShare(String CWListedShare) {
        this.CWListedShare = CWListedShare;
    }

    public String getsType() {
        return sType;
    }

    public void setsType(String sType) {
        this.sType = sType;
    }

    public String getsBenefit() {
        return sBenefit;
    }

    public void setsBenefit(String sBenefit) {
        this.sBenefit = sBenefit;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Timestamp getCrawledTime() {
        return crawledTime;
    }

    public void setCrawledTime(Timestamp crawledTime) {
        this.crawledTime = crawledTime;
    }
}
