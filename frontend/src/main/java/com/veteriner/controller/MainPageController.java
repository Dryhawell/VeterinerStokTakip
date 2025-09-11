package com.veteriner.controller;

import com.veteriner.model.*;
import com.veteriner.service.ApiService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainPageController {
    @FXML
    private Label kritikStokLabel;
    
    @FXML
    private Label sktLabel;
    
    @FXML
    private Label toplamUrunLabel;
    
    @FXML
    private Label toplamBorcLabel;

    @FXML
    private TableView<Urun> kritikStokTable;

    @FXML
    private TableView<Urun> sktTable;

    @FXML
    private TableView<Musteri> borcluMusterilerTable;

    // Kritik stok tablosu kolonları
    @FXML
    private TableColumn<Urun, String> kritikStokUrunAdiColumn;
    
    @FXML
    private TableColumn<Urun, Integer> kritikStokMevcutColumn;
    
    @FXML
    private TableColumn<Urun, Integer> kritikStokMinimumColumn;

    // SKT tablosu kolonları
    @FXML
    private TableColumn<Urun, String> sktUrunAdiColumn;
    
    @FXML
    private TableColumn<Urun, LocalDate> sktTarihiColumn;

    // Borçlu müşteriler tablosu kolonları
    @FXML
    private TableColumn<Musteri, String> borcluMusteriAdiColumn;
    
    @FXML
    private TableColumn<Musteri, Double> borcMiktariColumn;

    private ObservableList<Urun> kritikStokUrunler = FXCollections.observableArrayList();
    private ObservableList<Urun> sktUrunler = FXCollections.observableArrayList();
    private ObservableList<Musteri> borcluMusteriler = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTables();
        refreshDashboard();
    }

    private void setupTables() {
        // Kritik stok tablosu ayarları
        kritikStokUrunAdiColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAd()));
        kritikStokMevcutColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStokMiktari()));
        kritikStokMinimumColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getMinimumStok()));
        kritikStokTable.setItems(kritikStokUrunler);

        // SKT tablosu ayarları
        sktUrunAdiColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAd()));
        sktTarihiColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSonKullanmaTarihi()));
        sktTable.setItems(sktUrunler);

        // Borçlu müşteriler tablosu ayarları
        borcluMusteriAdiColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAd()));
        borcMiktariColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getBakiye()));
        borcluMusterilerTable.setItems(borcluMusteriler);
    }

    private void refreshDashboard() {
        ApiService.getInstance().getDashboard().thenAccept(response -> {
            javafx.application.Platform.runLater(() -> {
                // Kritik stok bilgileri
                kritikStokUrunler.setAll(response.getKritikStokUrunler());
                kritikStokLabel.setText(response.getKritikStokUrunler().size() + " ürün kritik seviyede");

                // SKT uyarıları
                sktUrunler.setAll(response.getSktYaklasanUrunler());
                sktLabel.setText(response.getSktYaklasanUrunler().size() + " ürünün SKT'si yaklaşıyor");

                // Toplam ürün ve borç bilgileri
                toplamUrunLabel.setText(response.getToplamUrunCesidi() + " çeşit ürün");
                toplamBorcLabel.setText(String.format("%.2f TL toplam borç", response.getToplamBorc()));

                // Borçlu müşteriler
                borcluMusteriler.setAll(response.getBorcluMusteriler());
            });
        }).exceptionally(throwable -> {
            javafx.application.Platform.runLater(() -> {
                // Hata durumunda UI'ı temizle
                kritikStokUrunler.clear();
                sktUrunler.clear();
                borcluMusteriler.clear();
                kritikStokLabel.setText("Hata: Veriler alınamadı");
                sktLabel.setText("");
                toplamUrunLabel.setText("");
                toplamBorcLabel.setText("");
            });
            return null;
        });
    }

    @FXML
    private void refreshClicked() {
        refreshDashboard();
    }
}
