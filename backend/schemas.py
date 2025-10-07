from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

class KategoriBase(BaseModel):
    ad: str
    aciklama: Optional[str] = None

class KategoriCreate(KategoriBase):
    pass

class Kategori(KategoriBase):
    id: int
    
    class Config:
        from_attributes = True

class UrunBase(BaseModel):
    ad: str
    aciklama: Optional[str] = None
    marka: Optional[str] = None
    birim: Optional[str] = None
    alis_fiyati: float
    satis_fiyati: float
    stok_miktari: int
    kritik_stok: int
    kategori_id: int

class UrunCreate(UrunBase):
    pass

class Urun(UrunBase):
    id: int
    
    class Config:
        from_attributes = True

class MusteriBase(BaseModel):
    ad: str
    soyad: str
    telefon: Optional[str] = None
    email: Optional[str] = None
    adres: Optional[str] = None
    borc: float = 0.0

class MusteriCreate(MusteriBase):
    pass

class Musteri(MusteriBase):
    id: int

    class Config:
        from_attributes = True

class SatisDetayBase(BaseModel):
    miktar: int
    birim_fiyat: float
    toplam_fiyat: float
    urun_id: int

class SatisDetayCreate(SatisDetayBase):
    pass

class SatisDetay(SatisDetayBase):
    id: int
    satis_id: int

    class Config:
        from_attributes = True

class SatisBase(BaseModel):
    toplam_tutar: float
    odenen_tutar: float
    kalan_tutar: float
    musteri_id: int
    detaylar: List[SatisDetayCreate]

class SatisCreate(SatisBase):
    pass

class Satis(SatisBase):
    id: int
    tarih: datetime
    detaylar: List[SatisDetay]

    class Config:
        from_attributes = True

class OdemeBase(BaseModel):
    tutar: float
    aciklama: Optional[str] = None
    musteri_id: int

class OdemeCreate(OdemeBase):
    pass

class Odeme(OdemeBase):
    id: int
    tarih: datetime

    class Config:
        from_attributes = True

class KritikUrun(BaseModel):
    id: int
    ad: str
    stok: int
    kritikStok: int

class BorcluMusteri(BaseModel):
    ad: str
    soyad: str
    borc: float

class SatisOzet(BaseModel):
    id: int
    tarih: datetime
    toplamTutar: float
    musteriAdi: str

class UrunSatis(BaseModel):
    id: int
    ad: str
    toplamSatis: int

class DashboardSchema(BaseModel):
    toplamBorc: float
    gunlukSatis: float
    aylikSatis: float
    kritikUrunler: List[KritikUrun]
    borcluMusteriler: List[BorcluMusteri]
    sonSatislar: List[SatisOzet]
    enCokSatilanUrunler: List[UrunSatis]
