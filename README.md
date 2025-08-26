# Veteriner Stok & Veresiye Takip Sistemi

Bu proje, küçük ölçekli veteriner klinikleri için geliştirilmiş bir stok ve veresiye takip sistemidir.

## Özellikler

- Ürün ve kategori yönetimi
- Stok giriş/çıkış takibi
- Müşteri kayıtları ve borç takibi
- Son kullanma tarihi takibi
- Minimum stok seviyesi uyarıları
- Raporlama sistemi (PDF/Excel)

## Teknolojiler

### Backend
- Python FastAPI
- SQLAlchemy (ORM)
- SQLite veritabanı

### Frontend
- Java (JavaFX)

## Kurulum

1. Gereksinimleri yükleyin:
```bash
pip install -r requirements.txt
```

2. Backend'i başlatın:
```bash
cd backend
python -m uvicorn main:app --reload
```

3. Frontend uygulamasını başlatın:
```bash
cd frontend
# Java uygulaması başlatma komutları eklenecek
```

## API Endpoints

### Kategoriler
- `GET /kategoriler/` - Tüm kategorileri listele
- `POST /kategoriler/` - Yeni kategori ekle
- `GET /kategoriler/{id}` - Kategori detayı

### Ürünler
- `GET /urunler/` - Tüm ürünleri listele
- `POST /urunler/` - Yeni ürün ekle
- `PUT /urunler/{id}` - Ürün güncelle
- `GET /urunler/{id}` - Ürün detayı

### Müşteriler
- `GET /musteriler/` - Tüm müşterileri listele
- `POST /musteriler/` - Yeni müşteri ekle
- `GET /musteriler/{id}` - Müşteri detayı

### Satışlar
- `POST /satislar/` - Yeni satış kaydı
- `GET /satislar/{id}` - Satış detayı

### Uyarılar
- `GET /uyarilar/` - Stok ve SKT uyarıları

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.
