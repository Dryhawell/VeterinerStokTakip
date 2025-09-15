from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from database import SessionLocal, engine, Base
import models
import schemas

app = FastAPI()

# CORS ayarlari
app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*']
)

# Veritabani tablolarini olustur
Base.metadata.create_all(bind=engine)

# Veritabani baglantisi icin dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.get('/')
def read_root():
    return {'message': 'Veteriner Stok Takip Sistemi API calisiyor'}

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

# Urun endpoint'leri
@app.post('/urunler/', response_model=schemas.Urun)
def create_urun(urun: schemas.UrunCreate, db: Session = Depends(get_db)):
    db_urun = models.Urun(**urun.dict())
    db.add(db_urun)
    db.commit()
    db.refresh(db_urun)
    return db_urun

@app.get('/urunler/', response_model=list[schemas.Urun])
def read_urunler(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    urunler = db.query(models.Urun).offset(skip).limit(limit).all()
    return urunler
