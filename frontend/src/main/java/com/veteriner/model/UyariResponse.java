package com.veteriner.model;

import java.util.List;
import java.time.LocalDate;

public class UyariResponse {
    private List<Urun> kritikStokUrunler;
    private List<Urun> sktYaklasanUrunler;
    private List<Musteri> borcluMusteriler;
    private int toplamUrunCesidi;
    private double toplamBorc;

    public List<Urun> getKritikStokUrunler() {
        return kritikStokUrunler;
    }

    public void setKritikStokUrunler(List<Urun> kritikStokUrunler) {
        this.kritikStokUrunler = kritikStokUrunler;
    }

    public List<Urun> getSktYaklasanUrunler() {
        return sktYaklasanUrunler;
    }

    public void setSktYaklasanUrunler(List<Urun> sktYaklasanUrunler) {
        this.sktYaklasanUrunler = sktYaklasanUrunler;
    }

    public List<Musteri> getBorcluMusteriler() {
        return borcluMusteriler;
    }

    public void setBorcluMusteriler(List<Musteri> borcluMusteriler) {
        this.borcluMusteriler = borcluMusteriler;
    }

    public int getToplamUrunCesidi() {
        return toplamUrunCesidi;
    }

    public void setToplamUrunCesidi(int toplamUrunCesidi) {
        this.toplamUrunCesidi = toplamUrunCesidi;
    }

    public double getToplamBorc() {
        return toplamBorc;
    }

    public void setToplamBorc(double toplamBorc) {
        this.toplamBorc = toplamBorc;
    }


}
