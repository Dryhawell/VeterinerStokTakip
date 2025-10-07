package com.veteriner.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import com.veteriner.MainApp;
import com.veteriner.service.ApiService;
import javafx.application.Platform;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardController {
    @FXML
    private Label gunlukSatisLabel;
    
    @FXML
    private Label aylikSatisLabel;
    
    @FXML
    private Label toplamBorcLabel;
    
    @FXML
    private ListView<String> kritikStokListView;
    
    @FXML
    private ListView<String> borcluMusterilerListView;
    
    @FXML
    private BarChart<String, Number> satislarChart;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("tr-TR"));

    @FXML
    private void initialize() {
        // İlk yükleme
        refreshDashboard();
    }

    public void setMainApp(MainApp mainApp) {
        // MainApp referansı şu an için kullanılmıyor
    }

    private void refreshDashboard() {
        // Label'ları sıfırla
        if (gunlukSatisLabel != null) gunlukSatisLabel.setText("0 TL");
        if (aylikSatisLabel != null) aylikSatisLabel.setText("0 TL");
        if (toplamBorcLabel != null) toplamBorcLabel.setText("0 TL");
        
        // ListView'ları temizle
        if (kritikStokListView != null) kritikStokListView.getItems().clear();
        if (borcluMusterilerListView != null) borcluMusterilerListView.getItems().clear();
        
        // Grafikleri temizle
        if (satislarChart != null) satislarChart.getData().clear();
        ApiService.getInstance().getDashboard()
            .thenAccept(dashboard -> Platform.runLater(() -> {
                try {
                    // Üst bilgi kartlarını güncelle
                    gunlukSatisLabel.setText(currencyFormatter.format(dashboard.getGunlukSatis()));
                    aylikSatisLabel.setText(currencyFormatter.format(dashboard.getAylikSatis()));
                    toplamBorcLabel.setText(currencyFormatter.format(dashboard.getToplamBorc()));
                    
                    // Kritik stok listesini güncelle
                    kritikStokListView.getItems().clear();
                    dashboard.getKritikUrunler().forEach(urun -> {
                        kritikStokListView.getItems().add(String.format("%s (Stok: %d, Kritik: %d)",
                            urun.getAd(), urun.getStok(), urun.getKritikStok()));
                    });
                    
                    // Son satışlar grafiğini güncelle
                    satislarChart.getData().clear();
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Satış Tutarı");
                    dashboard.getSonSatislar().forEach(satis -> {
                        series.getData().add(new XYChart.Data<>(
                            satis.getTarih().toString().split("T")[0], // ISO tarih formatından sadece tarih kısmını al
                            satis.getToplamTutar()
                        ));
                    });
                    satislarChart.getData().add(series);
                    
                    // En çok satılan ürünler grafiğini güncelle
                    XYChart.Series<String, Number> urunSeries = new XYChart.Series<>();
                    urunSeries.setName("Satış Adedi");
                    dashboard.getEnCokSatilanUrunler().forEach(urun -> {
                        urunSeries.getData().add(new XYChart.Data<>(
                            urun.getAd(),
                            urun.getToplamSatis()
                        ));
                    });
                    satislarChart.getData().add(urunSeries);
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Dashboard Hatası");
                    alert.setHeaderText("Dashboard verisi alınamadı");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }))
            .exceptionally(e -> {
                Platform.runLater(() -> {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Dashboard Hatası");
                    alert.setHeaderText("Dashboard verisi alınamadı");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
                return null;
            });
    }
}