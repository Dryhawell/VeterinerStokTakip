package com.veteriner.controller;

import com.veteriner.model.Urun;
import com.veteriner.model.Kategori;
import com.veteriner.service.ApiService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UrunController {
    @FXML
    private TextField aramaField;
    
    @FXML
    private TableView<Urun> urunTable;
    
    @FXML
    private TableColumn<Urun, Integer> idColumn;
    
    @FXML
    private TableColumn<Urun, String> adColumn;
    
    @FXML
    private TableColumn<Urun, String> kategoriColumn;
    
    @FXML
    private TableColumn<Urun, Integer> stokMiktariColumn;
    
    @FXML
    private TableColumn<Urun, Integer> minimumStokColumn;
    
    @FXML
    private TableColumn<Urun, Double> birimFiyatColumn;
    
    @FXML
    private TableColumn<Urun, LocalDate> sonKullanmaTarihiColumn;
    
    @FXML
    private TableColumn<Urun, Void> islemlerColumn;
    
    private ObservableList<Urun> urunler = FXCollections.observableArrayList();
    private FilteredList<Urun> filteredUrunler;
    private ApiService apiService;
    private ObservableList<Kategori> kategoriler = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() {
        apiService = ApiService.getInstance();
        setupTable();
        setupSearch();
        loadData();
    }
    
    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        adColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
        kategoriColumn.setCellValueFactory(cellData -> {
            Integer kategoriId = cellData.getValue().getKategoriId();
            Kategori kategori = kategoriler.stream()
                .filter(k -> k.getId().equals(kategoriId))
                .findFirst()
                .orElse(null);
            return new javafx.beans.property.SimpleStringProperty(
                kategori != null ? kategori.getAd() : "");
        });
        stokMiktariColumn.setCellValueFactory(new PropertyValueFactory<>("stokMiktari"));
        minimumStokColumn.setCellValueFactory(new PropertyValueFactory<>("minimumStok"));
        birimFiyatColumn.setCellValueFactory(new PropertyValueFactory<>("birimFiyat"));
        sonKullanmaTarihiColumn.setCellValueFactory(new PropertyValueFactory<>("sonKullanmaTarihi"));
        
        // İşlemler için butonlar
        islemlerColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button duzenleBtn = new Button("Düzenle");
            private final Button silBtn = new Button("Sil");
            private final HBox buttons = new HBox(5, duzenleBtn, silBtn);
            
            {
                duzenleBtn.getStyleClass().add("edit-button");
                silBtn.getStyleClass().add("delete-button");
                
                duzenleBtn.setOnAction(_ -> {
                    Urun urun = getTableView().getItems().get(getIndex());
                    handleUrunDuzenle(urun);
                });
                
                silBtn.setOnAction(_ -> {
                    Urun urun = getTableView().getItems().get(getIndex());
                    handleUrunSil(urun);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
        
        // Tarih formatını ekliyoruz
        sonKullanmaTarihiColumn.setCellFactory(_ -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        
        // Birim fiyat formatını ekliyoruz
        birimFiyatColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f ₺", item));
                }
            }
        });
        
        filteredUrunler = new FilteredList<>(urunler, _ -> true);
        urunTable.setItems(filteredUrunler);
    }
    
    private void setupSearch() {
        aramaField.textProperty().addListener((_, __, newValue) -> {
            filteredUrunler.setPredicate(urun -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (urun.getAd().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                
                Kategori kategori = kategoriler.stream()
                    .filter(k -> k.getId().equals(urun.getKategoriId()))
                    .findFirst()
                    .orElse(null);
                
                if (kategori != null && kategori.getAd().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false;
            });
        });
    }
    
    private void loadData() {
        // Kategorileri yükle
        apiService.getKategoriler().thenAccept(response -> {
            javafx.application.Platform.runLater(() -> {
                kategoriler.setAll(response);
                // Kategoriler yüklendikten sonra ürünleri yükle
                loadUrunler();
            });
        }).exceptionally(e -> {
            javafx.application.Platform.runLater(() -> {
                showError("Kategoriler yüklenirken bir hata oluştu", e.getMessage());
            });
            return null;
        });
    }
    
    private void loadUrunler() {
        apiService.getUrunler().thenAccept(response -> {
            javafx.application.Platform.runLater(() -> {
                urunler.setAll(response);
            });
        }).exceptionally(e -> {
            javafx.application.Platform.runLater(() -> {
                showError("Ürünler yüklenirken bir hata oluştu", e.getMessage());
            });
            return null;
        });
    }
    
    @FXML
    private void handleYeniUrun() {
        Dialog<Urun> dialog = createUrunDialog(null);
        Optional<Urun> result = dialog.showAndWait();
        
        result.ifPresent(yeniUrun -> {
            apiService.createUrun(yeniUrun).thenAccept(response -> {
                javafx.application.Platform.runLater(() -> {
                    urunler.add(response);
                    showInfo("Başarılı", "Ürün başarıyla eklendi");
                });
            }).exceptionally(e -> {
                javafx.application.Platform.runLater(() -> {
                    showError("Ürün eklenirken bir hata oluştu", e.getMessage());
                });
                return null;
            });
        });
    }
    
    private void handleUrunDuzenle(Urun urun) {
        Dialog<Urun> dialog = createUrunDialog(urun);
        Optional<Urun> result = dialog.showAndWait();
        
        result.ifPresent(guncelUrun -> {
            guncelUrun.setId(urun.getId());
            
            apiService.updateUrun(urun.getId(), guncelUrun).thenAccept(response -> {
                javafx.application.Platform.runLater(() -> {
                    int index = urunler.indexOf(urun);
                    urunler.set(index, response);
                    showInfo("Başarılı", "Ürün başarıyla güncellendi");
                });
            }).exceptionally(e -> {
                javafx.application.Platform.runLater(() -> {
                    showError("Ürün güncellenirken bir hata oluştu", e.getMessage());
                });
                return null;
            });
        });
    }
    
    private void handleUrunSil(Urun urun) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ürün Sil");
        alert.setHeaderText("Ürün Silme Onayı");
        alert.setContentText("'" + urun.getAd() + "' ürününü silmek istediğinize emin misiniz?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            apiService.deleteUrun(urun.getId()).thenAccept(_ -> {
                javafx.application.Platform.runLater(() -> {
                    urunler.remove(urun);
                    showInfo("Başarılı", "Ürün başarıyla silindi");
                });
            }).exceptionally(e -> {
                javafx.application.Platform.runLater(() -> {
                    showError("Ürün silinirken bir hata oluştu", e.getMessage());
                });
                return null;
            });
        }
    }
    
    private Dialog<Urun> createUrunDialog(Urun urun) {
        Dialog<Urun> dialog = new Dialog<>();
        dialog.setTitle(urun == null ? "Yeni Ürün" : "Ürün Düzenle");
        dialog.setHeaderText(urun == null ? "Yeni ürün ekle" : "Ürün bilgilerini düzenle");

        ButtonType kaydetButton = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(kaydetButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField adField = new TextField();
        adField.setPromptText("Ürün adı");
        if (urun != null) adField.setText(urun.getAd());

        ComboBox<Kategori> kategoriCombo = new ComboBox<>(kategoriler);
        kategoriCombo.setPromptText("Kategori seçin");
        kategoriCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Kategori kategori) {
                return kategori != null ? kategori.getAd() : "";
            }

            @Override
            public Kategori fromString(String string) {
                return null;
            }
        });
        if (urun != null) {
            kategoriler.stream()
                .filter(k -> k.getId().equals(urun.getKategoriId()))
                .findFirst()
                .ifPresent(kategoriCombo::setValue);
        }

        TextField stokMiktariField = new TextField();
        stokMiktariField.setPromptText("Stok miktarı");
        if (urun != null) stokMiktariField.setText(urun.getStokMiktari().toString());

        TextField minimumStokField = new TextField();
        minimumStokField.setPromptText("Minimum stok");
        if (urun != null) minimumStokField.setText(urun.getMinimumStok().toString());

        TextField birimFiyatField = new TextField();
        birimFiyatField.setPromptText("Birim fiyat");
        if (urun != null) birimFiyatField.setText(urun.getBirimFiyat().toString());

        DatePicker sktPicker = new DatePicker();
        sktPicker.setPromptText("Son kullanma tarihi");
        if (urun != null && urun.getSonKullanmaTarihi() != null) 
            sktPicker.setValue(urun.getSonKullanmaTarihi());

        grid.add(new Label("Ürün Adı:"), 0, 0);
        grid.add(adField, 1, 0);
        grid.add(new Label("Kategori:"), 0, 1);
        grid.add(kategoriCombo, 1, 1);
        grid.add(new Label("Stok Miktarı:"), 0, 2);
        grid.add(stokMiktariField, 1, 2);
        grid.add(new Label("Minimum Stok:"), 0, 3);
        grid.add(minimumStokField, 1, 3);
        grid.add(new Label("Birim Fiyat:"), 0, 4);
        grid.add(birimFiyatField, 1, 4);
        grid.add(new Label("Son Kullanma Tarihi:"), 0, 5);
        grid.add(sktPicker, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == kaydetButton) {
                try {
                    Kategori secilenKategori = kategoriCombo.getValue();
                    if (secilenKategori == null) {
                        showError("Hata", "Lütfen bir kategori seçin", "");
                        return null;
                    }

                    String ad = adField.getText().trim();
                    if (ad.isEmpty()) {
                        showError("Hata", "Lütfen ürün adını girin", "");
                        return null;
                    }
                    
                    Urun yeniUrun = new Urun();
                    yeniUrun.setAd(ad);
                    yeniUrun.setKategoriId(secilenKategori.getId());
                    yeniUrun.setStokMiktari(Integer.parseInt(stokMiktariField.getText()));
                    yeniUrun.setMinimumStok(Integer.parseInt(minimumStokField.getText()));
                    yeniUrun.setBirimFiyat(Double.parseDouble(birimFiyatField.getText()));
                    yeniUrun.setSonKullanmaTarihi(sktPicker.getValue());
                    return yeniUrun;
                } catch (NumberFormatException e) {
                    showError("Hata", "Lütfen sayısal değerleri doğru formatta girin", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }
    
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bilgi");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
