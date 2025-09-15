package com.veteriner.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.veteriner.MainApp;

/**
 * Ana pencere için controller sınıfı.
 * Menü çubuğunu ve ana layout'u yönetir.
 */
public class RootLayoutController {
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        // İnitialize işlemleri
    }

    @FXML
    private void handleShowDashboard() {
        mainApp.showDashboardView();
    }

    @FXML
    private void handleShowKategoriler() {
        mainApp.showKategoriView();
    }

    @FXML
    private void handleShowUrunler() {
        mainApp.showUrunView();
    }

    @FXML
    private void handleShowMusteriler() {
        mainApp.showMusteriView();
    }

    @FXML
    private void handleYeniSatis() {
        mainApp.showSatisView();
    }

    /**
     * Satışlar ekranını açar
     */
    @FXML
    private void handleShowSatislar() {
        mainApp.showSatisView();
    }

    /**
     * Stok raporu oluşturur
     */
    @FXML
    private void handleStokRaporu() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Stok Raporu");
        alert.setHeaderText(null);
        alert.setContentText("Stok raporu oluşturuluyor...");
        alert.showAndWait();
        // TODO: Stok raporu oluşturma işlemleri
    }

    /**
     * Satış raporu oluşturur
     */
    @FXML
    private void handleSatisRaporu() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Satış Raporu");
        alert.setHeaderText(null);
        alert.setContentText("Satış raporu oluşturuluyor...");
        alert.showAndWait();
        // TODO: Satış raporu oluşturma işlemleri
    }

    /**
     * Borç raporu oluşturur
     */
    @FXML
    private void handleBorcRaporu() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Müşteri Borç Raporu");
        alert.setHeaderText(null);
        alert.setContentText("Müşteri borç raporu oluşturuluyor...");
        alert.showAndWait();
        // TODO: Borç raporu oluşturma işlemleri
    }

    /**
     * Hakkında dialogunu gösterir
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Veteriner Stok Takip");
        alert.setHeaderText("Hakkında");
        alert.setContentText("Veteriner Klinikleri için Stok ve Veresiye Takip Sistemi\nSürüm 1.0");
        alert.showAndWait();
    }

    /**
     * Uygulamayı kapatır
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}