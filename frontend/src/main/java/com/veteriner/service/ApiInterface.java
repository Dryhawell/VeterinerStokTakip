package com.veteriner.service;

import com.veteriner.model.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiInterface {
    // Dashboard
    @GET("dashboard/")
    Call<Dashboard> getDashboard();

    // Kategori endpoints
    @GET("kategoriler/")
    Call<List<Kategori>> getKategoriler();

    @POST("kategoriler/")
    Call<Kategori> createKategori(@Body Kategori kategori);

    @PUT("kategoriler/{id}/")
    Call<Kategori> updateKategori(@Path("id") int id, @Body Kategori kategori);

    @DELETE("kategoriler/{id}/")
    Call<Void> deleteKategori(@Path("id") int id);

    // Ürün endpoints
    @GET("urunler/")
    Call<List<Urun>> getUrunler();

    @POST("urunler/")
    Call<Urun> createUrun(@Body Urun urun);

    @PUT("urunler/{id}/")
    Call<Urun> updateUrun(@Path("id") int id, @Body Urun urun);

    @DELETE("urunler/{id}/")
    Call<Void> deleteUrun(@Path("id") int id);

    // Müşteri endpoints
    @GET("musteriler/")
    Call<List<Musteri>> getMusteriler();

    @POST("musteriler/")
    Call<Musteri> createMusteri(@Body Musteri musteri);

    @PUT("musteriler/{id}/")
    Call<Musteri> updateMusteri(@Path("id") int id, @Body Musteri musteri);

    @DELETE("musteriler/{id}/")
    Call<Void> deleteMusteri(@Path("id") int id);

    // Satış endpoints
    @GET("satislar/")
    Call<List<Satis>> getSatislar();

    @POST("satislar/")
    Call<Satis> createSatis(@Body SatisCreate satis);

    @GET("satislar/{id}/")
    Call<Satis> getSatisById(@Path("id") Integer id);

    // Ödemeler
    @GET("odemeler/")
    Call<List<Odeme>> getOdemeler(@Query("musteri_id") Integer musteriId);

    @POST("odemeler/")
    Call<Odeme> createOdeme(@Body Odeme odeme);

    @GET("odemeler/{musteri_id}/")
    Call<List<Odeme>> getOdemelerByMusteriId(@Path("musteri_id") Integer musteriId);
}
