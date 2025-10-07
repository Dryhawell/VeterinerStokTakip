package com.veteriner.controller;

import com.veteriner.model.Kategori;
import com.veteriner.service.ApiService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Optional;

public class KategoriController {
    @FXML
    private TableView<Kategori> kategoriTable;
    
    @FXML
    private TableColumn<Kategori, Integer> idColumn;
    
    @FXML
    private TableColumn<Kategori, String> adColumn;
    
    @FXML
    private TableColumn<Kategori, Void> islemlerColumn;
    
    // Kategori listesi burada bulunmktadir
    
    private ObservableList<Kategori> kategoriler = FXCollections.observableArrayList();
    private ApiService apiService;
    
    @FXML
    private void initialize() {
        apiService = ApiService.getInstance();
        setupTable();
        loadKategoriler();
    }
    
    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        adColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
        
        // İşlemler kolonu için butonlar var
        islemlerColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button duzenleBtn = new Button("Düzenle");
            private final Button silBtn = new Button("Sil");
            private final HBox buttons = new HBox(5, duzenleBtn, silBtn);
            
            {
                duzenleBtn.getStyleClass().add("edit-button");
                silBtn.getStyleClass().add("delete-button");
                
                duzenleBtn.setOnAction(_ -> {
                    Kategori kategori = getTableView().getItems().get(getIndex());
                    handleKategoriDuzenle(kategori);
                });
                
                silBtn.setOnAction(_ -> {
                    Kategori kategori = getTableView().getItems().get(getIndex());
                    handleKategoriSil(kategori);
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
        
        kategoriTable.setItems(kategoriler);
    }
    
    private void loadKategoriler() {
        apiService.getKategoriler().thenAccept(response -> {
            javafx.application.Platform.runLater(() -> {
                kategoriler.setAll(response);
            });
        }).exceptionally(throwable -> {
            javafx.application.Platform.runLater(() -> {
                showError("Kategoriler yüklenirken bir hata oluştu", throwable.getMessage());
            });
            return null;
        });
    }
    
    @FXML
    private void handleYeniKategori() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Yeni Kategori");
        dialog.setHeaderText("Yeni kategori ekle");

        ButtonType kaydetButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(kaydetButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField kategoriAdi = new TextField();
        kategoriAdi.setPromptText("Kategori adı");
        grid.add(new Label("Kategori Adı:"), 0, 0);
        grid.add(kategoriAdi, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == kaydetButtonType) {
                return kategoriAdi.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(ad -> {
            if (!ad.trim().isEmpty()) {
                Kategori yeniKategori = new Kategori();
                yeniKategori.setAd(ad.trim());
                
                apiService.createKategori(yeniKategori).thenAccept(response -> {
                    javafx.application.Platform.runLater(() -> {
                        kategoriler.add(response);
                        showInfo("Başarılı", "Kategori başarıyla eklendi");
                    });
                }).exceptionally(throwable -> {
                    javafx.application.Platform.runLater(() -> {
                        showError("Kategori eklenirken bir hata oluştu", throwable.getMessage());
                    });
                    return null;
                });
            }
        });
    }
    
    private void handleKategoriDuzenle(Kategori kategori) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Kategori Düzenle");
        dialog.setHeaderText("Kategori adını düzenle");

        ButtonType kaydetButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(kaydetButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField kategoriAdi = new TextField(kategori.getAd());
        grid.add(new Label("Kategori Adı:"), 0, 0);
        grid.add(kategoriAdi, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == kaydetButtonType) {
                return kategoriAdi.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(yeniAd -> {
            if (!yeniAd.trim().isEmpty()) {
                Kategori guncelKategori = new Kategori();
                guncelKategori.setId(kategori.getId());
                guncelKategori.setAd(yeniAd.trim());
                
                apiService.updateKategori(kategori.getId(), guncelKategori).thenAccept(response -> {
                    javafx.application.Platform.runLater(() -> {
                        int index = kategoriler.indexOf(kategori);
                        kategoriler.set(index, response);
                        showInfo("Başarılı", "Kategori başarıyla güncellendi");
                    });
                }).exceptionally(throwable -> {
                    javafx.application.Platform.runLater(() -> {
                        showError("Kategori güncellenirken bir hata oluştu", throwable.getMessage());
                    });
                    return null;
                });
            }
        });
    }
    
    private void handleKategoriSil(Kategori kategori) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Kategori Sil");
        alert.setHeaderText("Kategori Silme Onayı");
        alert.setContentText("'" + kategori.getAd() + "' kategorisini silmek istediğinize emin misiniz?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            apiService.deleteKategori(kategori.getId()).thenAccept(_ -> {
                javafx.application.Platform.runLater(() -> {
                    kategoriler.remove(kategori);
                    showInfo("Başarılı", "Kategori başarıyla silindi");
                });
            }).exceptionally(throwable -> {
                javafx.application.Platform.runLater(() -> {
                    showError("Kategori silinirken bir hata oluştu", throwable.getMessage());
                });
                return null;
            });
        }
    }
    
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
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
