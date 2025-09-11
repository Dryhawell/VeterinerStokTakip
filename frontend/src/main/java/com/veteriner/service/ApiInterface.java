package com.veteriner.service;

import com.veteriner.model.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiInterface {
    // Dashboard ve Uyarılar
    @GET("/dashboard")
    Call<UyariResponse> getDashboard();

    // Kategori endpoints
    @GET("/kategoriler")
    Call<List<Kategori>> getKategoriler();

    @POST("/kategoriler")
    Call<Kategori> createKategori(@Body Kategori kategori);

    @PUT("/kategoriler/{id}")
    Call<Kategori> updateKategori(@Path("id") Integer id, @Body Kategori kategori);

    @DELETE("/kategoriler/{id}")
    Call<Void> deleteKategori(@Path("id") Integer id);

    // Ürün endpoints
    @GET("/urunler")
    Call<List<Urun>> getUrunler();

    @POST("/urunler")
    Call<Urun> createUrun(@Body Urun urun);

    @PUT("/urunler/{id}")
    Call<Urun> updateUrun(@Path("id") Integer id, @Body Urun urun);

    @DELETE("/urunler/{id}")
    Call<Void> deleteUrun(@Path("id") Integer id);

    // Müşteri endpoints
    @GET("/musteriler")
    Call<List<Musteri>> getMusteriler();

    @POST("/musteriler")
    Call<Musteri> createMusteri(@Body Musteri musteri);

    @PUT("/musteriler/{id}")
    Call<Musteri> updateMusteri(@Path("id") Integer id, @Body Musteri musteri);

    @DELETE("/musteriler/{id}")
    Call<Void> deleteMusteri(@Path("id") Integer id);

    // Satış endpoints
    @GET("/satislar")
    Call<List<Satis>> getSatislar();

    @POST("/satislar")
    Call<Satis> createSatis(@Body Satis satis);

    @GET("/satislar/{id}")
    Call<Satis> getSatisById(@Path("id") Integer id);
}
