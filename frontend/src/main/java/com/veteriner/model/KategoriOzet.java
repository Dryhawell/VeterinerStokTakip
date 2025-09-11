package com.veteriner.model;

public class KategoriOzet {
    private String ad;
    private int urunSayisi;
    
    public KategoriOzet() {
    }
    
    public KategoriOzet(String ad, int urunSayisi) {
        this.ad = ad;
        this.urunSayisi = urunSayisi;
    }
    
    public String getAd() {
        return ad;
    }
    
    public void setAd(String ad) {
        this.ad = ad;
    }
    
    public int getUrunSayisi() {
        return urunSayisi;
    }
    
    public void setUrunSayisi(int urunSayisi) {
        this.urunSayisi = urunSayisi;
    }
}
