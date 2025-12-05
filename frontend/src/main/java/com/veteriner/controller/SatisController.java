package com.veteriner.controller;

import com.veteriner.model.*;
import com.veteriner.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.text.DecimalFormat;

public class SatisController implements Initializable {
    private final ApiService apiService = ApiService.getInstance();
    private final DecimalFormat paraFormat = new DecimalFormat("#,##0.00");

    @FXML private ComboBox<Musteri> cmbMusteri;
    @FXML private Label lblMusteriBorc;
    @FXML private ComboBox<Urun> cmbUrun;
    @FXML private TextField txtMiktar;
    @FXML private TextField txtBirimFiyat;
    @FXML private TableView<SepetItem> sepetTable;
    @FXML private TableColumn<SepetItem, String> colUrunAd;
    @FXML private TableColumn<SepetItem, Integer> colMiktar;
    @FXML private TableColumn<SepetItem, Double> colBirimFiyat;
    @FXML private TableColumn<SepetItem, Double> colToplamFiyat;
    @FXML private TableColumn<SepetItem, Void> colIslemler;
    @FXML private Label lblToplamTutar;
    @FXML private TextField txtOdenenTutar;
    @FXML private TextField txtKalanTutar;
    @FXML private Button btnSatisiTamamla;
    @FXML private Label lblStatus;

    private ObservableList<Musteri> musteriler = FXCollections.observableArrayList();
    private ObservableList<Urun> urunler = FXCollections.observableArrayList();
    private ObservableList<SepetItem> sepetItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupTableColumns();
        setupListeners();
        loadData();
    }

    private void setupComboBoxes() {
        // Müşteri ComboBox
        cmbMusteri.setConverter(new StringConverter<Musteri>() {
            @Override
            public String toString(Musteri musteri) {
                return musteri != null ? musteri.getAd() + " " + musteri.getSoyad() : "";
            }

            @Override
            public Musteri fromString(String string) {
                return null;
            }
        });

        cmbMusteri.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblMusteriBorc.setText("Mevcut Borç: " + paraFormat.format(newVal.getBorc()) + " TL");
            } else {
                lblMusteriBorc.setText("Mevcut Borç: 0,00 TL");
            }
        });

        // Ürün ComboBox
        cmbUrun.setConverter(new StringConverter<Urun>() {
            @Override
            public String toString(Urun urun) {
                return urun != null ? urun.getAd() + " (" + urun.getStokMiktari() + " " + urun.getBirim() + ")" : "";
            }

            @Override
            public Urun fromString(String string) {
                return null;
            }
        });

        cmbUrun.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtBirimFiyat.setText(String.valueOf(newVal.getSatisFiyati()));
            } else {
                txtBirimFiyat.clear();
            }
        });
    }

    private void setupTableColumns() {
        colUrunAd.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getUrun().getAd()
            )
        );
        colMiktar.setCellValueFactory(new PropertyValueFactory<>("miktar"));
        colBirimFiyat.setCellValueFactory(new PropertyValueFactory<>("birimFiyat"));
        colToplamFiyat.setCellValueFactory(new PropertyValueFactory<>("toplamFiyat"));
        
        colIslemler.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Sil");

            {
                deleteButton.setOnAction(event -> {
                    SepetItem item = getTableRow().getItem();
                    if (item != null) {
                        sepetItems.remove(item);
                        updateToplam();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        sepetTable.setItems(sepetItems);
    }

    private void setupListeners() {
        // Miktar oran şeysi
        txtMiktar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtMiktar.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        // Ödenen tutar değişikliği
        txtOdenenTutar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtOdenenTutar.setText(oldVal);
                return;
            }
            updateKalanTutar();
        });
    }

    private void loadData() {
        // Müşterileri yükle
        apiService.getMusteriler()
            .thenAccept(list -> {
                musteriler.setAll(list);
                cmbMusteri.setItems(musteriler);
            })
            .exceptionally(e -> {
                updateStatus("Müşteriler yüklenirken hata: " + e.getMessage());
                return null;
            });

        // Ürünleri yükle
        apiService.getUrunler()
            .thenAccept(list -> {
                urunler.setAll(list);
                cmbUrun.setItems(urunler);
            })
            .exceptionally(e -> {
                updateStatus("Ürünler yüklenirken hata: " + e.getMessage());
                return null;
            });
    }

    @FXML
    private void handleUrunEkle() {
        if (!validateUrunForm()) {
            return;
        }

        Urun urun = cmbUrun.getValue();
        int miktar = Integer.parseInt(txtMiktar.getText());
        double birimFiyat = urun.getSatisFiyati();

        // Stok kontrolü
        if (miktar > urun.getStokMiktari()) {
            updateStatus("Stok yetersiz. Mevcut stok: " + urun.getStokMiktari());
            return;
        }

        // Aynı ürün varsa miktarını güncelle
        boolean urunBulundu = false;
        for (SepetItem item : sepetItems) {
            if (item.getUrun().getId().equals(urun.getId())) {
                int yeniMiktar = item.getMiktar() + miktar;
                if (yeniMiktar > urun.getStokMiktari()) {
                    updateStatus("Toplam miktar stok miktarını aşıyor");
                    return;
                }
                item.setMiktar(yeniMiktar);
                item.setToplamFiyat(yeniMiktar * birimFiyat);
                urunBulundu = true;
                break;
            }
        }

        // Yeni ürün ekle
        if (!urunBulundu) {
            SepetItem yeniItem = new SepetItem();
            yeniItem.setUrun(urun);
            yeniItem.setMiktar(miktar);
            yeniItem.setBirimFiyat(birimFiyat);
            yeniItem.setToplamFiyat(miktar * birimFiyat);
            sepetItems.add(yeniItem);
        }

        updateToplam();
        clearUrunForm();
    }

    private boolean validateUrunForm() {
        if (cmbUrun.getValue() == null) {
            updateStatus("Lütfen bir ürün seçiniz");
            return false;
        }

        if (txtMiktar.getText().isEmpty()) {
            updateStatus("Lütfen miktar giriniz");
            return false;
        }

        try {
            int miktar = Integer.parseInt(txtMiktar.getText());
            if (miktar <= 0) {
                updateStatus("Miktar sıfırdan büyük olmalıdır");
                return false;
            }
        } catch (NumberFormatException e) {
            updateStatus("Geçerli bir miktar giriniz");
            return false;
        }

        return true;
    }

    private void updateToplam() {
        double toplam = sepetItems.stream()
            .mapToDouble(SepetItem::getToplamFiyat)
            .sum();

        lblToplamTutar.setText(paraFormat.format(toplam) + " TL");
        btnSatisiTamamla.setDisable(sepetItems.isEmpty() || cmbMusteri.getValue() == null);
        txtOdenenTutar.setText("0");
        updateKalanTutar();
    }

    private void updateKalanTutar() {
        try {
            double toplam = sepetItems.stream()
                .mapToDouble(SepetItem::getToplamFiyat)
                .sum();
            double odenen = txtOdenenTutar.getText().isEmpty() ? 0 : 
                           Double.parseDouble(txtOdenenTutar.getText());
            double kalan = toplam - odenen;
            
            txtKalanTutar.setText(paraFormat.format(kalan));
        } catch (NumberFormatException e) {
            txtKalanTutar.setText("0,00");
        }
    }

    private void clearUrunForm() {
        cmbUrun.setValue(null);
        txtMiktar.clear();
        txtBirimFiyat.clear();
    }

    @FXML
    private void handleSatisiTamamla() {
        if (!validateSatisForm()) {
            return;
        }

        // Satış detaylarını hazırla
        List<SatisDetayCreate> detaylar = new ArrayList<>();
        for (SepetItem item : sepetItems) {
            SatisDetayCreate detay = new SatisDetayCreate();
            detay.setMiktar(item.getMiktar());
            detay.setBirimFiyat(item.getBirimFiyat());
            detay.setToplamFiyat(item.getToplamFiyat());
            detay.setUrunId(item.getUrun().getId());
            detaylar.add(detay);
        }

        // Satış nesnesini oluştur
        SatisCreate satis = new SatisCreate();
        satis.setMusteriId(cmbMusteri.getValue().getId());
        satis.setToplamTutar(sepetItems.stream().mapToDouble(SepetItem::getToplamFiyat).sum());
        satis.setOdenenTutar(Double.parseDouble(txtOdenenTutar.getText()));
        satis.setKalanTutar(Double.parseDouble(txtKalanTutar.getText()));
        satis.setDetaylar(detaylar);

        // Satışı kaydet
        apiService.createSatis(satis)
            .thenAccept(kaydedilenSatis -> {
                updateStatus("Satış başarıyla tamamlandı");
                clearForm();
            })
            .exceptionally(e -> {
                updateStatus("Hata: " + e.getMessage());
                return null;
            });
    }

    private boolean validateSatisForm() {
        if (cmbMusteri.getValue() == null) {
            updateStatus("Lütfen müşteri seçin!");
            return false;
        }

        if (sepetItems.isEmpty()) {
            updateStatus("Sepet boş!");
            return false;
        }

        if (txtOdenenTutar.getText().isEmpty()) {
            updateStatus("Lütfen ödenen tutarı girin!");
            return false;
        }

        try {
            double odenen = Double.parseDouble(txtOdenenTutar.getText());
            double toplam = sepetItems.stream()
                .mapToDouble(SepetItem::getToplamFiyat)
                .sum();

            if (odenen < 0) {
                updateStatus("Ödenen tutar 0'dan küçük olamaz!");
                return false;
            }

            if (odenen > toplam) {
                updateStatus("Ödenen tutar toplam tutardan büyük olamaz!");
                return false;
            }
        } catch (NumberFormatException e) {
            updateStatus("Geçerli bir ödeme tutarı girin!");
            return false;
        }

        return true;
    }

    @FXML
    private void handleIptal() {
        clearForm();
    }

    private void clearForm() {
        cmbMusteri.setValue(null);
        clearUrunForm();
        sepetItems.clear();
        txtOdenenTutar.clear();
        txtKalanTutar.clear();
        lblToplamTutar.setText("0,00 TL");
        btnSatisiTamamla.setDisable(true);
    }

    private void updateStatus(String message) {
        if (lblStatus != null) {
            lblStatus.setText(message);
        }
    }

    // --- FXML'de tanımlı handler'lar için köprü metodları ---
    // SatisView.fxml: onAction="#handleSepeteEkle" -> ürün ekleme
    @FXML
    private void handleSepeteEkle() {
        handleUrunEkle();
    }

    // SatisView.fxml: onAction="#handleUrunCikar" -> seçili ürünü sepetten çıkar
    @FXML
    private void handleUrunCikar() {
        SepetItem selected = sepetTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            sepetItems.remove(selected);
            updateToplam();
        } else {
            updateStatus("Lütfen sepetten bir ürün seçin!");
        }
    }

    // SatisView.fxml: onAction="#handleSepetiTemizle" -> sepeti temizle
    @FXML
    private void handleSepetiTemizle() {
        sepetItems.clear();
        updateToplam();
    }

    // SatisView.fxml: onAction="#handleYeniMusteri" -> geçici bilgi (MainApp referansı yok)
    @FXML
    private void handleYeniMusteri() {
        updateStatus("Yeni müşteri ekleme ekranı henüz bağlanmadı.");
    }

    // Sepet öğesi için yardımcı sınıf
    public static class SepetItem {
        private Urun urun;
        private int miktar;
        private double birimFiyat;
        private double toplamFiyat;

        public Urun getUrun() { return urun; }
        public void setUrun(Urun urun) { this.urun = urun; }
        
        public int getMiktar() { return miktar; }
        public void setMiktar(int miktar) { this.miktar = miktar; }
        
        public double getBirimFiyat() { return birimFiyat; }
        public void setBirimFiyat(double birimFiyat) { this.birimFiyat = birimFiyat; }
        
        public double getToplamFiyat() { return toplamFiyat; }
        public void setToplamFiyat(double toplamFiyat) { this.toplamFiyat = toplamFiyat; }
    }
}