package com.veteriner.model;

import java.time.LocalDate;

public class Urun {
    private Integer id;
    private String ad;
    private Integer kategoriId;
    private Integer stokMiktari;
    private Integer minimumStok;
    private Double birimFiyat;
    private LocalDate sonKullanmaTarihi;
    private Double satisFiyati;
    private String birim;

    // Getter ve Setter ekliyoruz
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public Integer getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(Integer kategoriId) {
        this.kategoriId = kategoriId;
    }

    public Integer getStokMiktari() {
        return stokMiktari;
    }

    public void setStokMiktari(Integer stokMiktari) {
        this.stokMiktari = stokMiktari;
    }

    public Integer getMinimumStok() {
        return minimumStok;
    }

    public void setMinimumStok(Integer minimumStok) {
        this.minimumStok = minimumStok;
    }

    public Double getBirimFiyat() {
        return birimFiyat;
    }

    public void setBirimFiyat(Double birimFiyat) {
        this.birimFiyat = birimFiyat;
    }

    public LocalDate getSonKullanmaTarihi() {
        return sonKullanmaTarihi;
    }

    public void setSonKullanmaTarihi(LocalDate sonKullanmaTarihi) {
        this.sonKullanmaTarihi = sonKullanmaTarihi;
    }

    public Double getSatisFiyati() {
        return satisFiyati;
    }

    public void setSatisFiyati(Double satisFiyati) {
        this.satisFiyati = satisFiyati;
    }

    public String getBirim() {
        return birim;
    }

    public void setBirim(String birim) {
        this.birim = birim;
    }
}
