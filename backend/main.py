from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from sqlalchemy import func
from database import SessionLocal, engine, Base
import models
import schemas
from datetime import datetime, timedelta

app = FastAPI(title="Veteriner Stok Takip Sistemi API")

# CORS ayarları
app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*']
)

# Veritabanı tablolarını oluştur
Base.metadata.create_all(bind=engine)

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

# Kategori endpoint'leri
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
    
    # İlişkili kayıtları kontrol et
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

# Ürün endpoint'leri
@app.post('/urunler/', response_model=schemas.Urun)
def create_urun(urun: schemas.UrunCreate, db: Session = Depends(get_db)):
    # Kategori kontrolü
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
    
    # Kategori kontrolü
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
    
    # Satış kontrolü
    if len(db_urun.satis_detaylari) > 0:
        raise HTTPException(
            status_code=400, 
            detail='Bu ürüne ait satış kayıtları bulunmaktadır. Ürün silinemez.'
        )
    
    db.delete(db_urun)
    db.commit()
    return {'message': 'Ürün başarıyla silindi'}

# Müşteri endpoint'leri
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

# Satış endpoint'leri
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
    db.flush()  # ID'yi almak için flush yapıyoruz
    
    # Satış detaylarını ekle ve stok kontrolü yap
    for detay in satis.detaylar:
        # Ürünü kontrol et ve stok yeterliliğini doğrula
        urun = db.query(models.Urun).filter(models.Urun.id == detay.urun_id).first()
        if not urun:
            raise HTTPException(status_code=404, detail=f'Ürün bulunamadı: {detay.urun_id}')
        
        if urun.stok_miktari < detay.miktar:
            raise HTTPException(
                status_code=400, 
                detail=f'Yetersiz stok. Ürün: {urun.ad}, Mevcut: {urun.stok_miktari}, İstenen: {detay.miktar}'
            )
        
        # Satış detayını ekle
        db_detay = models.SatisDetay(
            satis_id=db_satis.id,
            urun_id=detay.urun_id,
            miktar=detay.miktar,
            birim_fiyat=detay.birim_fiyat,
            toplam_fiyat=detay.miktar * detay.birim_fiyat
        )
        db.add(db_detay)
        
        # Stok miktarını güncelle
        urun.stok_miktari -= detay.miktar
        
        # Eğer stok kritik seviyenin altına düştüyse uyarı ekle
        if urun.stok_miktari <= urun.kritik_stok:
            print(f"UYARI: {urun.ad} stok seviyesi kritik seviyenin altında! Mevcut: {urun.stok_miktari}")
    
    # Müşteri borcunu güncelle
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

# Ödeme endpoint'leri
@app.post('/odemeler/', response_model=schemas.Odeme)
def create_odeme(odeme: schemas.OdemeCreate, db: Session = Depends(get_db)):
    # Müşteriyi kontrol et
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
    
    # Müşteri borcunu güncelle
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

# Dashboard endpoint'i
@app.get('/dashboard/')
def get_dashboard(db: Session = Depends(get_db)):
    # Kritik stok seviyesindeki ürünler
    kritik_urunler = db.query(models.Urun).filter(
        models.Urun.stok_miktari <= models.Urun.kritik_stok
    ).all()
    
    # Toplam borç durumu
    toplam_borc = db.query(func.sum(models.Musteri.borc)).scalar() or 0
    
    # Günlük satış toplamı
    bugun = datetime.now().date()
    gunluk_satis = db.query(func.sum(models.Satis.toplam_tutar)).filter(
        func.date(models.Satis.tarih) == bugun
    ).scalar() or 0
    
    # Aylık satış toplamı
    ay_basi = datetime(bugun.year, bugun.month, 1).date()
    aylik_satis = db.query(func.sum(models.Satis.toplam_tutar)).filter(
        func.date(models.Satis.tarih) >= ay_basi
    ).scalar() or 0
    
    # Son satışlar
    son_satislar = db.query(models.Satis).order_by(
        models.Satis.tarih.desc()
    ).limit(5).all()
    
    # En çok satılan ürünler (son 30 gün)
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
    
    return {
        'kritik_urunler': [
            {
                'id': urun.id,
                'ad': urun.ad,
                'stok': urun.stok_miktari,
                'kritik_stok': urun.kritik_stok
            }
            for urun in kritik_urunler
        ],
        'toplam_borc': toplam_borc,
        'gunluk_satis': gunluk_satis,
        'aylik_satis': aylik_satis,
        'son_satislar': [
            {
                'id': satis.id,
                'tarih': satis.tarih,
                'toplam_tutar': satis.toplam_tutar,
                'musteri_adi': f'{satis.musteri.ad} {satis.musteri.soyad}'
            }
            for satis in son_satislar
        ],
        'en_cok_satilan_urunler': [
            {
                'id': urun.id,
                'ad': urun.ad,
                'toplam_satis': toplam_satis
            }
            for urun, toplam_satis in en_cok_satilan_urunler
        ]
    }