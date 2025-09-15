package com.veteriner.service;

import com.veteriner.model.*;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import retrofit2.Response;

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

    public CompletableFuture<Dashboard> getDashboard() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Dashboard> response = api.getDashboard().execute();
                return response.body();
            } catch (Exception e) {
                e.printStackTrace();
                return new Dashboard();
            }
        });
    }

    public CompletableFuture<List<Kategori>> getKategoriler() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Kategori>> response = api.getKategoriler().execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Kategoriler alınamadı");
            } catch (Exception e) {
                throw new RuntimeException("Kategoriler alınamadı", e);
            }
        });
    }

    public CompletableFuture<Kategori> createKategori(Kategori kategori) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Kategori> response = api.createKategori(kategori).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Kategori eklenemedi");
            } catch (Exception e) {
                throw new RuntimeException("Kategori eklenemedi", e);
            }
        });
    }

    public CompletableFuture<Kategori> updateKategori(Integer id, Kategori kategori) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Kategori> response = api.updateKategori(id, kategori).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Kategori güncellenemedi");
            } catch (Exception e) {
                throw new RuntimeException("Kategori güncellenemedi", e);
            }
        });
    }

    public CompletableFuture<Void> deleteKategori(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Void> response = api.deleteKategori(id).execute();
                if (response.isSuccessful()) {
                    return null;
                }
                throw new RuntimeException("Kategori silinemedi");
            } catch (Exception e) {
                throw new RuntimeException("Kategori silinemedi", e);
            }
        });
    }

    public CompletableFuture<List<Urun>> getUrunler() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Urun>> response = api.getUrunler().execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Ürünler alınamadı");
            } catch (Exception e) {
                throw new RuntimeException("Ürünler alınamadı", e);
            }
        });
    }

    public CompletableFuture<Urun> createUrun(Urun urun) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Urun> response = api.createUrun(urun).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Ürün eklenemedi");
            } catch (Exception e) {
                throw new RuntimeException("Ürün eklenemedi", e);
            }
        });
    }

    public CompletableFuture<Urun> updateUrun(Integer id, Urun urun) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Urun> response = api.updateUrun(id, urun).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Ürün güncellenemedi");
            } catch (Exception e) {
                throw new RuntimeException("Ürün güncellenemedi", e);
            }
        });
    }

    public CompletableFuture<Void> deleteUrun(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Void> response = api.deleteUrun(id).execute();
                if (response.isSuccessful()) {
                    return null;
                }
                throw new RuntimeException("Ürün silinemedi");
            } catch (Exception e) {
                throw new RuntimeException("Ürün silinemedi", e);
            }
        });
    }

    public CompletableFuture<List<Musteri>> getMusteriler() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Musteri>> response = api.getMusteriler().execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Müşteriler alınamadı");
            } catch (Exception e) {
                throw new RuntimeException("Müşteriler alınamadı", e);
            }
        });
    }

    public CompletableFuture<Musteri> createMusteri(Musteri musteri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Musteri> response = api.createMusteri(musteri).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Müşteri eklenemedi");
            } catch (Exception e) {
                throw new RuntimeException("Müşteri eklenemedi", e);
            }
        });
    }

    public CompletableFuture<Musteri> updateMusteri(Integer id, Musteri musteri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Musteri> response = api.updateMusteri(id, musteri).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Müşteri güncellenemedi");
            } catch (Exception e) {
                throw new RuntimeException("Müşteri güncellenemedi", e);
            }
        });
    }

    public CompletableFuture<Boolean> deleteMusteri(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Void> response = api.deleteMusteri(id).execute();
                return response.isSuccessful();
            } catch (Exception e) {
                throw new RuntimeException("Müşteri silinemedi", e);
            }
        });
    }

    public CompletableFuture<List<Satis>> getSatislar() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Satis>> response = api.getSatislar().execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Satışlar alınamadı");
            } catch (Exception e) {
                throw new RuntimeException("Satışlar alınamadı", e);
            }
        });
    }

    public CompletableFuture<Satis> createSatis(SatisCreate satisCreate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Satis> response = api.createSatis(satisCreate).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Satış kaydedilemedi");
            } catch (Exception e) {
                throw new RuntimeException("Satış kaydedilemedi", e);
            }
        });
    }

    public CompletableFuture<Satis> getSatisById(Integer id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Satis> response = api.getSatisById(id).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Satış bulunamadı");
            } catch (Exception e) {
                throw new RuntimeException("Satış bulunamadı", e);
            }
        });
    }

    public CompletableFuture<List<Odeme>> getOdemeler(Integer musteriId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Odeme>> response = api.getOdemelerByMusteriId(musteriId).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Ödemeler alınamadı");
            } catch (Exception e) {
                throw new RuntimeException("Ödemeler alınamadı", e);
            }
        });
    }

    public CompletableFuture<Odeme> createOdeme(Odeme odeme) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<Odeme> response = api.createOdeme(odeme).execute();
                if (response.isSuccessful()) {
                    return response.body();
                }
                throw new RuntimeException("Ödeme kaydedilemedi");
            } catch (Exception e) {
                throw new RuntimeException("Ödeme kaydedilemedi", e);
            }
        });
    }
}
