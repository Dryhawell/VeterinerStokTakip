package com.veteriner.model;

import java.util.List;

public class Dashboard {
    private double gunlukSatis;
    private double aylikSatis;
    private double toplamBorc;
    private List<KritikUrun> kritikUrunler;
    private List<BorcluMusteri> borcluMusteriler;
    private List<SatisOzet> sonSatislar;
    private List<UrunSatis> enCokSatilanUrunler;

    // Getters ve Setters
    public double getGunlukSatis() {
        return gunlukSatis;
    }

    public void setGunlukSatis(double gunlukSatis) {
        this.gunlukSatis = gunlukSatis;
    }

    public double getAylikSatis() {
        return aylikSatis;
    }

    public void setAylikSatis(double aylikSatis) {
        this.aylikSatis = aylikSatis;
    }

    public double getToplamBorc() {
        return toplamBorc;
    }

    public void setToplamBorc(double toplamBorc) {
        this.toplamBorc = toplamBorc;
    }

    public List<KritikUrun> getKritikUrunler() {
        return kritikUrunler;
    }

    public void setKritikUrunler(List<KritikUrun> kritikUrunler) {
        this.kritikUrunler = kritikUrunler;
    }

    public List<BorcluMusteri> getBorcluMusteriler() {
        return borcluMusteriler;
    }

    public void setBorcluMusteriler(List<BorcluMusteri> borcluMusteriler) {
        this.borcluMusteriler = borcluMusteriler;
    }

    public List<SatisOzet> getSonSatislar() {
        return sonSatislar;
    }

    public void setSonSatislar(List<SatisOzet> sonSatislar) {
        this.sonSatislar = sonSatislar;
    }

    public List<UrunSatis> getEnCokSatilanUrunler() {
        return enCokSatilanUrunler;
    }

    public void setEnCokSatilanUrunler(List<UrunSatis> enCokSatilanUrunler) {
        this.enCokSatilanUrunler = enCokSatilanUrunler;
    }

    // İç sınıflar
    public static class KritikUrun {
        private String ad;
        private int stok;
        private int kritikStok;

        public String getAd() {
            return ad;
        }

        public void setAd(String ad) {
            this.ad = ad;
        }

        public int getStok() {
            return stok;
        }

        public void setStok(int stok) {
            this.stok = stok;
        }

        public int getKritikStok() {
            return kritikStok;
        }

        public void setKritikStok(int kritikStok) {
            this.kritikStok = kritikStok;
        }
    }

    public static class BorcluMusteri {
        private String adSoyad;
        private double borc;

        public String getAdSoyad() {
            return adSoyad;
        }

        public void setAdSoyad(String adSoyad) {
            this.adSoyad = adSoyad;
        }

        public double getBorc() {
            return borc;
        }

        public void setBorc(double borc) {
            this.borc = borc;
        }
    }

    public static class SatisOzet {
        private String tarih;
        private double toplamTutar;
        private String musteriAdi;

        public String getTarih() {
            return tarih;
        }

        public void setTarih(String tarih) {
            this.tarih = tarih;
        }

        public double getToplamTutar() {
            return toplamTutar;
        }

        public void setToplamTutar(double toplamTutar) {
            this.toplamTutar = toplamTutar;
        }

        public String getMusteriAdi() {
            return musteriAdi;
        }

        public void setMusteriAdi(String musteriAdi) {
            this.musteriAdi = musteriAdi;
        }
    }

    public static class UrunSatis {
        private String ad;
        private int toplamSatis;

        public String getAd() {
            return ad;
        }

        public void setAd(String ad) {
            this.ad = ad;
        }

        public int getToplamSatis() {
            return toplamSatis;
        }

        public void setToplamSatis(int toplamSatis) {
            this.toplamSatis = toplamSatis;
        }
    }
}