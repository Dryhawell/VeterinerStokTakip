package com.veteriner.model;

public class SatisDetay {
    private Integer id;
    private Integer satisId;
    private Integer urunId;
    private Integer miktar;
    private Double birimFiyat;

    // Getter ve Setter ekliyoruz
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSatisId() {
        return satisId;
    }

    public void setSatisId(Integer satisId) {
        this.satisId = satisId;
    }

    public Integer getUrunId() {
        return urunId;
    }

    public void setUrunId(Integer urunId) {
        this.urunId = urunId;
    }

    public Integer getMiktar() {
        return miktar;
    }

    public void setMiktar(Integer miktar) {
        this.miktar = miktar;
    }

    public Double getBirimFiyat() {
        return birimFiyat;
    }

    public void setBirimFiyat(Double birimFiyat) {
        this.birimFiyat = birimFiyat;
    }
}
