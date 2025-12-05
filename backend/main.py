import uvicorn
from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from sqlalchemy import func
from database import SessionLocal, engine, Base
import models
import schemas
from datetime import datetime, timedelta, date
from typing import List
import logging

# Logging ayarları
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# FastAPI uygulamasını oluştur
app = FastAPI(
    title="Veteriner Stok Takip Sistemi API",
    debug=True
)

# CORS ayarları
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

@app.on_event("startup")
async def startup_event():
    logger.info("Uygulama başlatılıyor...")
    try:
        db = SessionLocal()
        # Veritabanı boşsa örnek veriler ekle
        if db.query(models.Kategori).count() == 0:
            logger.info("Örnek veriler ekleniyor...")
            
            # Örnek kategoriler
            kategoriler = [
                {"ad": "İlaçlar", "aciklama": "Her türlü veteriner ilacı"},
                {"ad": "Aşılar", "aciklama": "Koruyucu aşılar"},
                {"ad": "Vitamin ve Takviyeler", "aciklama": "Besin takviyeleri"},
                {"ad": "Tıbbi Malzemeler", "aciklama": "Sarf malzemeleri"}
            ]
            for k in kategoriler:
                kategori = models.Kategori(**k)
                db.add(kategori)
            db.commit()
            
            # Örnek ürünler
            urunler = [
                {"ad": "Antibiyotik A", "marka": "VetPharma", "kategori_id": 1, "alis_fiyati": 50.0, "satis_fiyati": 75.0, "stok_miktari": 100, "kritik_stok": 20},
                {"ad": "Karma Aşı", "marka": "VetVac", "kategori_id": 2, "alis_fiyati": 30.0, "satis_fiyati": 45.0, "stok_miktari": 50, "kritik_stok": 10},
                {"ad": "Multivitamin", "marka": "PetVit", "kategori_id": 3, "alis_fiyati": 25.0, "satis_fiyati": 40.0, "stok_miktari": 75, "kritik_stok": 15},
                {"ad": "Bandaj", "marka": "MedVet", "kategori_id": 4, "alis_fiyati": 10.0, "satis_fiyati": 15.0, "stok_miktari": 200, "kritik_stok": 50}
            ]
            for u in urunler:
                urun = models.Urun(**u)
                db.add(urun)
            db.commit()
            
            logger.info("Örnek veriler başarıyla eklendi")
        else:
            logger.info("Veritabanında veriler mevcut, örnek veri ekleme atlanıyor")
            
        db.close()
        logger.info("Veritabanı bağlantısı başarılı")
    except Exception as e:
        logger.error(f"Veritabanı bağlantı hatası: {e}")
        logger.debug(f"Hata detayları: {str(e.__class__.__name__)}: {str(e)}")
        raise

@app.on_event("shutdown")
async def shutdown_event():
    logger.info("Uygulama kapatılıyor...")

# Startup eventi geliyor
# Veritabanı işlemleri için dependency işlemi uygulanacak
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Veritabanı tablolarını oluştur
try:
    Base.metadata.create_all(bind=engine)
    
    # Örnek veriler ekleme
    db = SessionLocal()
    
    # Veritabanı boşsa örnek veriler ekle
    if db.query(models.Kategori).count() == 0:
        # Kategori ekleme
        kategori = models.Kategori(ad="Antibiyotikler", aciklama="Bakteriyel enfeksiyonları tedavi eden ilaçlar")
        db.add(kategori)
        db.commit()
        
        # Ürün ekleme
        urun = models.Urun(
            ad="Amoksisilin 500mg",
            aciklama="Geniş spektrumlu antibiyotik",
            marka="VetPharma",
            birim="Tablet",
            alis_fiyati=10.0,
            satis_fiyati=15.0,
            stok_miktari=100,
            kritik_stok=20,
            kategori_id=kategori.id
        )
        db.add(urun)
        
        # Müşteri ekleme
        musteri = models.Musteri(
            ad="Ahmet",
            soyad="Yılmaz",
            telefon="05551234567",
            email="ahmet@example.com",
            adres="İstanbul",
            borc=0.0
        )
        db.add(musteri)
        
        db.commit()
        logger.info("Veritabanı tabloları ve örnek veriler başarıyla oluşturuldu")
    else:
        logger.info("Veritabanı tabloları başarıyla oluşturuldu")
except Exception as e:
    logger.error(f"Veritabanı oluşturma hatası: {e}")
    raise

# Veritabanı bağlantısı için dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.get('/')
def read_root():
    return {'message': 'Veteriner Stok Takip Sistemi API çalışıyor'}

# Kategori endpoint'leri bulunuyor
@app.post('/kategoriler/', response_model=schemas.Kategori)
def create_kategori(kategori: schemas.KategoriCreate, db: Session = Depends(get_db)):
    db_kategori = models.Kategori(**kategori.dict())
    db.add(db_kategori)
    db.commit()
    db.refresh(db_kategori)
    return db_kategori

@app.get('/kategoriler/', response_model=list[schemas.Kategori])
def read_kategoriler(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    kategoriler = db.query(models.Kategori).offset(skip).limit(limit).all()
    return kategoriler

@app.get('/kategoriler/{kategori_id}', response_model=schemas.Kategori)
def read_kategori(kategori_id: int, db: Session = Depends(get_db)):
    kategori = db.query(models.Kategori).filter(models.Kategori.id == kategori_id).first()
    if kategori is None:
        raise HTTPException(status_code=404, detail='Kategori bulunamadı')
    return kategori

@app.put('/kategoriler/{kategori_id}', response_model=schemas.Kategori)
def update_kategori(kategori_id: int, kategori: schemas.KategoriCreate, db: Session = Depends(get_db)):
    db_kategori = db.query(models.Kategori).filter(models.Kategori.id == kategori_id).first()
    if db_kategori is None:
        raise HTTPException(status_code=404, detail='Kategori bulunamadı')
    
    # İlişkili kayıtları kontrol ediliyor
    if len(db_kategori.urunler) > 0:
        raise HTTPException(
            status_code=400, 
            detail='Bu kategoriye ait ürünler bulunmaktadır. Önce ürünleri başka kategoriye taşıyın.'
        )
    
    for key, value in kategori.dict().items():
        setattr(db_kategori, key, value)
    
    db.commit()
    db.refresh(db_kategori)
    return db_kategori

@app.delete('/kategoriler/{kategori_id}')
def delete_kategori(kategori_id: int, db: Session = Depends(get_db)):
    db_kategori = db.query(models.Kategori).filter(models.Kategori.id == kategori_id).first()
    if db_kategori is None:
        raise HTTPException(status_code=404, detail='Kategori bulunamadı')
    
    if len(db_kategori.urunler) > 0:
        raise HTTPException(
            status_code=400, 
            detail='Bu kategoriye ait ürünler bulunmaktadır. Önce ürünleri başka kategoriye taşıyın.'
        )
    
    db.delete(db_kategori)
    db.commit()
    return {'message': 'Kategori başarıyla silindi'}

# Ürün endpointleri bulunuyor
@app.post('/urunler/', response_model=schemas.Urun)
def create_urun(urun: schemas.UrunCreate, db: Session = Depends(get_db)):
    # Kategori kontrolü bulunuyor
    kategori = db.query(models.Kategori).filter(models.Kategori.id == urun.kategori_id).first()
    if not kategori:
        raise HTTPException(status_code=404, detail='Kategori bulunamadı')
    
    db_urun = models.Urun(**urun.dict())
    db.add(db_urun)
    db.commit()
    db.refresh(db_urun)
    return db_urun

@app.get('/urunler/', response_model=list[schemas.Urun])
def read_urunler(
    skip: int = 0, 
    limit: int = 100,
    kategori_id: int = None,
    kritik_stok: bool = False,
    db: Session = Depends(get_db)
):
    query = db.query(models.Urun)
    
    if kategori_id:
        query = query.filter(models.Urun.kategori_id == kategori_id)
    
    if kritik_stok:
        query = query.filter(models.Urun.stok_miktari <= models.Urun.kritik_stok)
    
    urunler = query.offset(skip).limit(limit).all()
    return urunler

@app.get('/urunler/{urun_id}', response_model=schemas.Urun)
def read_urun(urun_id: int, db: Session = Depends(get_db)):
    urun = db.query(models.Urun).filter(models.Urun.id == urun_id).first()
    if urun is None:
        raise HTTPException(status_code=404, detail='Ürün bulunamadı')
    return urun

@app.put('/urunler/{urun_id}', response_model=schemas.Urun)
def update_urun(urun_id: int, urun: schemas.UrunCreate, db: Session = Depends(get_db)):
    db_urun = db.query(models.Urun).filter(models.Urun.id == urun_id).first()
    if db_urun is None:
        raise HTTPException(status_code=404, detail='Ürün bulunamadı')
    
    # Kategori kontrolü yapar
    if urun.kategori_id != db_urun.kategori_id:
        kategori = db.query(models.Kategori).filter(models.Kategori.id == urun.kategori_id).first()
        if not kategori:
            raise HTTPException(status_code=404, detail='Kategori bulunamadı')
    
    for key, value in urun.dict().items():
        setattr(db_urun, key, value)
    
    db.commit()
    db.refresh(db_urun)
    return db_urun

@app.delete('/urunler/{urun_id}')
def delete_urun(urun_id: int, db: Session = Depends(get_db)):
    db_urun = db.query(models.Urun).filter(models.Urun.id == urun_id).first()
    if db_urun is None:
        raise HTTPException(status_code=404, detail='Ürün bulunamadı')
    
    # Satış kontrolünü üstlenir
    if len(db_urun.satis_detaylari) > 0:
        raise HTTPException(
            status_code=400, 
            detail='Bu ürüne ait satış kayıtları bulunmaktadır. Ürün silinemez.'
        )
    
    db.delete(db_urun)
    db.commit()
    return {'message': 'Ürün başarıyla silindi'}

# Müşteri endpointleri bulunuyor
@app.post('/musteriler/', response_model=schemas.Musteri)
def create_musteri(musteri: schemas.MusteriCreate, db: Session = Depends(get_db)):
    db_musteri = models.Musteri(**musteri.dict())
    db.add(db_musteri)
    db.commit()
    db.refresh(db_musteri)
    return db_musteri

@app.get('/musteriler/', response_model=list[schemas.Musteri])
def read_musteriler(
    skip: int = 0, 
    limit: int = 100,
    borclu: bool = False,
    db: Session = Depends(get_db)
):
    query = db.query(models.Musteri)
    
    if borclu:
        query = query.filter(models.Musteri.borc > 0)
    
    musteriler = query.offset(skip).limit(limit).all()
    return musteriler

@app.get('/musteriler/{musteri_id}', response_model=schemas.Musteri)
def read_musteri(musteri_id: int, db: Session = Depends(get_db)):
    musteri = db.query(models.Musteri).filter(models.Musteri.id == musteri_id).first()
    if musteri is None:
        raise HTTPException(status_code=404, detail='Müşteri bulunamadı')
    return musteri

@app.put('/musteriler/{musteri_id}', response_model=schemas.Musteri)
def update_musteri(musteri_id: int, musteri: schemas.MusteriCreate, db: Session = Depends(get_db)):
    db_musteri = db.query(models.Musteri).filter(models.Musteri.id == musteri_id).first()
    if db_musteri is None:
        raise HTTPException(status_code=404, detail='Müşteri bulunamadı')
    
    for key, value in musteri.dict().items():
        setattr(db_musteri, key, value)
    
    db.commit()
    db.refresh(db_musteri)
    return db_musteri

@app.delete('/musteriler/{musteri_id}')
def delete_musteri(musteri_id: int, db: Session = Depends(get_db)):
    db_musteri = db.query(models.Musteri).filter(models.Musteri.id == musteri_id).first()
    if db_musteri is None:
        raise HTTPException(status_code=404, detail='Müşteri bulunamadı')
    
    # Satış ve ödeme kontrolü
    if len(db_musteri.satislar) > 0 or len(db_musteri.odemeler) > 0:
        raise HTTPException(
            status_code=400, 
            detail='Bu müşteriye ait satış veya ödeme kayıtları bulunmaktadır. Müşteri silinemez.'
        )
    
    db.delete(db_musteri)
    db.commit()
    return {'message': 'Müşteri başarıyla silindi'}

# Satış endpointleri bulunuyor
@app.post('/satislar/', response_model=schemas.Satis)
def create_satis(satis: schemas.SatisCreate, db: Session = Depends(get_db)):
    # Müşteriyi kontrol et
    musteri = db.query(models.Musteri).filter(models.Musteri.id == satis.musteri_id).first()
    if not musteri:
        raise HTTPException(status_code=404, detail='Müşteri bulunamadı')
    
    # Satış kaydını oluştur
    db_satis = models.Satis(
        musteri_id=satis.musteri_id,
        toplam_tutar=satis.toplam_tutar,
        odenen_tutar=satis.odenen_tutar,
        kalan_tutar=satis.toplam_tutar - satis.odenen_tutar,
        tarih=datetime.now()
    )
    db.add(db_satis)
    db.flush()  # IDyi almak için flush yapıyoruz
    
    # Satış detaylarını ekle ve stok kontrolü yap
    for detay in satis.detaylar:
        # Ürünü kontrol et ve stok yeterliliğini doğrulama yap
        urun = db.query(models.Urun).filter(models.Urun.id == detay.urun_id).first()
        if not urun:
            raise HTTPException(status_code=404, detail=f'Ürün bulunamadı: {detay.urun_id}')
        
        if urun.stok_miktari < detay.miktar:
            raise HTTPException(
                status_code=400, 
                detail=f'Yetersiz stok. Ürün: {urun.ad}, Mevcut: {urun.stok_miktari}, İstenen: {detay.miktar}'
            )
        
        # Satış detayını ekledik
        db_detay = models.SatisDetay(
            satis_id=db_satis.id,
            urun_id=detay.urun_id,
            miktar=detay.miktar,
            birim_fiyat=detay.birim_fiyat,
            toplam_fiyat=detay.miktar * detay.birim_fiyat
        )
        db.add(db_detay)
        
        # Stok miktarını güncellediğimiz yer
        urun.stok_miktari -= detay.miktar
        
        # Eğer stok kritik seviyenin altına düştüyse uyarı getirir
        if urun.stok_miktari <= urun.kritik_stok:
            print(f"UYARI: {urun.ad} stok seviyesi kritik seviyenin altında! Mevcut: {urun.stok_miktari}")
    
    # Müşteri borcunu günceller
    if db_satis.kalan_tutar > 0:
        musteri.borc += db_satis.kalan_tutar
    
    db.commit()
    db.refresh(db_satis)
    return db_satis

@app.get('/satislar/', response_model=list[schemas.Satis])
def read_satislar(
    skip: int = 0, 
    limit: int = 100, 
    musteri_id: int = None,
    baslangic_tarihi: datetime = None,
    bitis_tarihi: datetime = None,
    db: Session = Depends(get_db)
):
    query = db.query(models.Satis)
    
    if musteri_id:
        query = query.filter(models.Satis.musteri_id == musteri_id)
    
    if baslangic_tarihi:
        query = query.filter(models.Satis.tarih >= baslangic_tarihi)
    
    if bitis_tarihi:
        query = query.filter(models.Satis.tarih <= bitis_tarihi)
    
    satislar = query.order_by(models.Satis.tarih.desc()).offset(skip).limit(limit).all()
    return satislar

@app.get('/satislar/{satis_id}', response_model=schemas.Satis)
def read_satis(satis_id: int, db: Session = Depends(get_db)):
    satis = db.query(models.Satis).filter(models.Satis.id == satis_id).first()
    if satis is None:
        raise HTTPException(status_code=404, detail='Satış bulunamadı')
    return satis

# Ödeme endpointleri bulunuyor
@app.post('/odemeler/', response_model=schemas.Odeme)
def create_odeme(odeme: schemas.OdemeCreate, db: Session = Depends(get_db)):
    # Müşteriyi kontrol etmeye yarar
    musteri = db.query(models.Musteri).filter(models.Musteri.id == odeme.musteri_id).first()
    if not musteri:
        raise HTTPException(status_code=404, detail='Müşteri bulunamadı')
    
    if odeme.tutar <= 0:
        raise HTTPException(status_code=400, detail='Ödeme tutarı 0\'dan büyük olmalıdır')
    
    if odeme.tutar > musteri.borc:
        raise HTTPException(
            status_code=400, 
            detail=f'Ödeme tutarı müşteri borcundan büyük olamaz. Mevcut borç: {musteri.borc}'
        )
    
    # Ödeme kaydını oluştur
    db_odeme = models.Odeme(
        musteri_id=odeme.musteri_id,
        tutar=odeme.tutar,
        aciklama=odeme.aciklama,
        tarih=datetime.now()
    )
    db.add(db_odeme)
    
    # Müşteri borcunu günceller
    musteri.borc -= odeme.tutar
    
    db.commit()
    db.refresh(db_odeme)
    return db_odeme

@app.get('/odemeler/', response_model=list[schemas.Odeme])
def read_odemeler(
    skip: int = 0, 
    limit: int = 100, 
    musteri_id: int = None,
    baslangic_tarihi: datetime = None,
    bitis_tarihi: datetime = None,
    db: Session = Depends(get_db)
):
    query = db.query(models.Odeme)
    
    if musteri_id:
        query = query.filter(models.Odeme.musteri_id == musteri_id)
    
    if baslangic_tarihi:
        query = query.filter(models.Odeme.tarih >= baslangic_tarihi)
    
    if bitis_tarihi:
        query = query.filter(models.Odeme.tarih <= bitis_tarihi)
    
    odemeler = query.order_by(models.Odeme.tarih.desc()).offset(skip).limit(limit).all()
    return odemeler

# Dashboard endpointini verir
@app.get('/dashboard/', response_model=schemas.DashboardSchema)
def get_dashboard(db: Session = Depends(get_db)):
    # Kritik stok seviyesindeki ürünler
    kritik_urunler = db.query(models.Urun).filter(
        models.Urun.stok_miktari <= models.Urun.kritik_stok
    ).all()
    
    # Toplam borç durumunu gösterir
    toplam_borc = db.query(func.sum(models.Musteri.borc)).scalar() or 0
    
    # Günlük satış toplamını gösterir 
    bugun = datetime.now().date()
    gunluk_satis = db.query(func.sum(models.Satis.toplam_tutar)).filter(
        func.date(models.Satis.tarih) == bugun
    ).scalar() or 0
    
    # Aylık satış toplamını gösterir
    ay_basi = datetime(bugun.year, bugun.month, 1).date()
    aylik_satis = db.query(func.sum(models.Satis.toplam_tutar)).filter(
        func.date(models.Satis.tarih) >= ay_basi
    ).scalar() or 0
    
    # Son satışlar bulunuyor
    son_satislar = db.query(models.Satis).order_by(
        models.Satis.tarih.desc()
    ).limit(5).all()
    
    # En çok satılan ürünler (son 30 gün için geçerlidir.)
    otuz_gun_once = datetime.now() - timedelta(days=30)
    en_cok_satilan_urunler = db.query(
        models.Urun.id,
        models.Urun.ad,
        func.sum(models.SatisDetay.miktar).label('toplam_satis')
    ).join(
        models.SatisDetay
    ).join(
        models.Satis
    ).filter(
        models.Satis.tarih >= otuz_gun_once
    ).group_by(
        models.Urun.id
    ).order_by(
        func.sum(models.SatisDetay.miktar).desc()
    ).limit(5).all()
    
    # Borçlu müşteriler
    borclu_musteriler = db.query(models.Musteri).filter(
        models.Musteri.borc > 0
    ).order_by(models.Musteri.borc.desc()).limit(10).all()
    
    return {
        'kritikUrunler': [
            {
                'id': urun.id,
                'ad': urun.ad,
                'stok': urun.stok_miktari,
                'kritikStok': urun.kritik_stok
            }
            for urun in kritik_urunler
        ],
        'toplamBorc': toplam_borc,
        'gunlukSatis': gunluk_satis,
        'aylikSatis': aylik_satis,
        'borcluMusteriler': [
            {
                'ad': musteri.ad,
                'soyad': musteri.soyad,
                'borc': musteri.borc
            }
            for musteri in borclu_musteriler
        ],
        'sonSatislar': [
            {
                'id': satis.id,
                'tarih': satis.tarih,
                'toplamTutar': satis.toplam_tutar,
                'musteriAdi': f'{satis.musteri.ad} {satis.musteri.soyad}'
            }
            for satis in son_satislar
        ],
        'enCokSatilanUrunler': [
            {
                'id': urun_id,
                'ad': urun_ad,
                'toplamSatis': toplam_satis
            }
            for urun_id, urun_ad, toplam_satis in en_cok_satilan_urunler
        ]
    }

if __name__ == "__main__":
    # Uvicorn yapılandırması
    config = uvicorn.Config(
        app=app,
        host="127.0.0.1",
        port=8081,
        reload=True,
        reload_dirs=["backend"],
        log_level="debug",
        access_log=True,
        workers=1
    )
    server = uvicorn.Server(config)
    try:
        logger.info("Sunucu başlatılıyor...")
        server.run()
    except Exception as e:
        logger.error(f"Sunucu hatası: {e}")
        raise