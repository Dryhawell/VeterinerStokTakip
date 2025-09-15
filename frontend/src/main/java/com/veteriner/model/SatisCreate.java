package com.veteriner.model;

import java.util.List;

public class SatisCreate {
    private Integer musteriId;
    private Double toplamTutar;
    private Double odenenTutar;
    private Double kalanTutar;
    private List<SatisDetayCreate> detaylar;

    public Integer getMusteriId() {
        return musteriId;
    }

    public void setMusteriId(Integer musteriId) {
        this.musteriId = musteriId;
    }

    public Double getToplamTutar() {
        return toplamTutar;
    }

    public void setToplamTutar(Double toplamTutar) {
        this.toplamTutar = toplamTutar;
    }

    public Double getOdenenTutar() {
        return odenenTutar;
    }

    public void setOdenenTutar(Double odenenTutar) {
        this.odenenTutar = odenenTutar;
    }

    public Double getKalanTutar() {
        return kalanTutar;
    }

    public void setKalanTutar(Double kalanTutar) {
        this.kalanTutar = kalanTutar;
    }

    public List<SatisDetayCreate> getDetaylar() {
        return detaylar;
    }

    public void setDetaylar(List<SatisDetayCreate> detaylar) {
        this.detaylar = detaylar;
    }
}