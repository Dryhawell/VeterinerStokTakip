package com.veteriner.service;

import com.veteriner.model.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8000";
    private static ApiService instance;
    private final ApiInterface api;

    private ApiService() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        api = retrofit.create(ApiInterface.class);
    }

    public static synchronized ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    // Dashboard ve Uyarılar
    public CompletableFuture<UyariResponse> getDashboard() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.getDashboard().execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return new UyariResponse();
            }
        });
    }

    // Kategori metodları
    public CompletableFuture<List<Kategori>> getKategoriler() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.getKategoriler().execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        });
    }

    public CompletableFuture<Kategori> createKategori(Kategori kategori) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.createKategori(kategori).execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Kategori> updateKategori(Integer id, Kategori kategori) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.updateKategori(id, kategori).execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> deleteKategori(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                api.deleteKategori(id).execute();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    // Ürün metodları
    public CompletableFuture<List<Urun>> getUrunler() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.getUrunler().execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        });
    }

    public CompletableFuture<Urun> createUrun(Urun urun) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.createUrun(urun).execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Urun> updateUrun(Integer id, Urun urun) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.updateUrun(id, urun).execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> deleteUrun(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                api.deleteUrun(id).execute();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    // Müşteri metodları
    public CompletableFuture<List<Musteri>> getMusteriler() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.getMusteriler().execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        });
    }

    public CompletableFuture<Musteri> createMusteri(Musteri musteri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.createMusteri(musteri).execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Musteri> updateMusteri(Integer id, Musteri musteri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.updateMusteri(id, musteri).execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> deleteMusteri(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                api.deleteMusteri(id).execute();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    // Satış metodları
    public CompletableFuture<List<Satis>> getSatislar() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.getSatislar().execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        });
    }

    public CompletableFuture<Satis> createSatis(Satis satis) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.createSatis(satis).execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Satis> getSatisById(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.getSatisById(id).execute().body();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}
