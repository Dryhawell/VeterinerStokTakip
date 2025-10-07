package com.veteriner.model;

import java.time.LocalDateTime;
import java.util.List;

public class Satis {
    private Integer id;
    private Integer musteriId;
    private Double toplamTutar;
    private LocalDateTime tarih;
    private List<SatisDetay> satisDetaylari;

    // Getter ve Setter ekliyom
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public LocalDateTime getTarih() {
        return tarih;
    }

    public void setTarih(LocalDateTime tarih) {
        this.tarih = tarih;
    }

    public List<SatisDetay> getSatisDetaylari() {
        return satisDetaylari;
    }

    public void setSatisDetaylari(List<SatisDetay> satisDetaylari) {
        this.satisDetaylari = satisDetaylari;
    }
}
