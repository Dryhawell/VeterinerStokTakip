from sqlalchemy import Column, Integer, String, Float, ForeignKey, DateTime, Boolean
from sqlalchemy.orm import relationship
from database import Base
from datetime import datetime

class Kategori(Base):
    __tablename__ = 'kategoriler'

    id = Column(Integer, primary_key=True, index=True)
    ad = Column(String, unique=True, index=True)
    aciklama = Column(String, nullable=True)
    urunler = relationship('Urun', back_populates='kategori')

class Urun(Base):
    __tablename__ = 'urunler'

    id = Column(Integer, primary_key=True, index=True)
    ad = Column(String, index=True)
    aciklama = Column(String, nullable=True)
    marka = Column(String, nullable=True)
    birim = Column(String, nullable=True)
    alis_fiyati = Column(Float)
    satis_fiyati = Column(Float)
    stok_miktari = Column(Integer)
    kritik_stok = Column(Integer)
    kategori_id = Column(Integer, ForeignKey('kategoriler.id'))
    kategori = relationship('Kategori', back_populates='urunler')
    satis_detaylari = relationship('SatisDetay', back_populates='urun')

class Musteri(Base):
    __tablename__ = 'musteriler'

    id = Column(Integer, primary_key=True, index=True)
    ad = Column(String, index=True)
    soyad = Column(String, index=True)
    telefon = Column(String, nullable=True)
    email = Column(String, nullable=True)
    adres = Column(String, nullable=True)
    borc = Column(Float, default=0.0)
    satislar = relationship('Satis', back_populates='musteri')
    odemeler = relationship('Odeme', back_populates='musteri')

class Satis(Base):
    __tablename__ = 'satislar'

    id = Column(Integer, primary_key=True, index=True)
    tarih = Column(DateTime, default=datetime.now)
    toplam_tutar = Column(Float)
    odenen_tutar = Column(Float)
    kalan_tutar = Column(Float)
    musteri_id = Column(Integer, ForeignKey('musteriler.id'))
    musteri = relationship('Musteri', back_populates='satislar')
    detaylar = relationship('SatisDetay', back_populates='satis')

class SatisDetay(Base):
    __tablename__ = 'satis_detaylari'

    id = Column(Integer, primary_key=True, index=True)
    miktar = Column(Integer)
    birim_fiyat = Column(Float)
    toplam_fiyat = Column(Float)
    satis_id = Column(Integer, ForeignKey('satislar.id'))
    urun_id = Column(Integer, ForeignKey('urunler.id'))
    satis = relationship('Satis', back_populates='detaylar')
    urun = relationship('Urun', back_populates='satis_detaylari')

class Odeme(Base):
    __tablename__ = 'odemeler'

    id = Column(Integer, primary_key=True, index=True)
    tarih = Column(DateTime, default=datetime.now)
    tutar = Column(Float)
    aciklama = Column(String, nullable=True)
    musteri_id = Column(Integer, ForeignKey('musteriler.id'))
    musteri = relationship('Musteri', back_populates='odemeler')
