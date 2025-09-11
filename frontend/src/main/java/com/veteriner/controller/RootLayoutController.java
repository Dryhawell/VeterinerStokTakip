package com.veteriner.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import com.veteriner.MainApp;
import javafx.scene.layout.AnchorPane;

public class RootLayoutController {
    @FXML
    private BorderPane rootLayout;
    
    @FXML
    private MenuItem urunListesiMenuItem;
    
    @FXML
    private MenuItem yeniUrunMenuItem;
    
    @FXML
    private MenuItem kategorilerMenuItem;
    
    @FXML
    private MenuItem stokGirisMenuItem;
    
    @FXML
    private MenuItem stokCikisMenuItem;
    
    @FXML
    private MenuItem stokHareketleriMenuItem;
    
    @FXML
    private MenuItem musteriListesiMenuItem;
    
    @FXML
    private MenuItem yeniMusteriMenuItem;
    
    @FXML
    private MenuItem yeniSatisMenuItem;
    
    @FXML
    private MenuItem satisGecmisiMenuItem;
    
    @FXML
    private MenuItem stokRaporuMenuItem;
    
    @FXML
    private MenuItem satisRaporuMenuItem;
    
    @FXML
    private MenuItem borcRaporuMenuItem;

    @FXML
    private void initialize() {
        // Event handler'ları ayarla
        urunListesiMenuItem.setOnAction(event -> handleUrunListesi());
        yeniUrunMenuItem.setOnAction(event -> handleYeniUrun());
        kategorilerMenuItem.setOnAction(event -> handleKategoriler());
        stokGirisMenuItem.setOnAction(event -> handleStokGiris());
        stokCikisMenuItem.setOnAction(event -> handleStokCikis());
        stokHareketleriMenuItem.setOnAction(event -> handleStokHareketleri());
        musteriListesiMenuItem.setOnAction(event -> handleMusteriListesi());
        yeniMusteriMenuItem.setOnAction(event -> handleYeniMusteri());
        yeniSatisMenuItem.setOnAction(event -> handleYeniSatis());
        satisGecmisiMenuItem.setOnAction(event -> handleSatisGecmisi());
        stokRaporuMenuItem.setOnAction(event -> handleStokRaporu());
        satisRaporuMenuItem.setOnAction(event -> handleSatisRaporu());
        borcRaporuMenuItem.setOnAction(event -> handleBorcRaporu());
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(fxmlPath));
            AnchorPane page = (AnchorPane) loader.load();
            rootLayout.setCenter(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUrunListesi() {
        loadPage("/fxml/UrunListesi.fxml");
    }

    private void handleYeniUrun() {
        loadPage("/fxml/YeniUrun.fxml");
    }

    private void handleKategoriler() {
        loadPage("/fxml/Kategoriler.fxml");
    }

    private void handleStokGiris() {
        loadPage("/fxml/StokGiris.fxml");
    }

    private void handleStokCikis() {
        loadPage("/fxml/StokCikis.fxml");
    }

    private void handleStokHareketleri() {
        loadPage("/fxml/StokHareketleri.fxml");
    }

    private void handleMusteriListesi() {
        loadPage("/fxml/MusteriListesi.fxml");
    }

    private void handleYeniMusteri() {
        loadPage("/fxml/YeniMusteri.fxml");
    }

    private void handleYeniSatis() {
        loadPage("/fxml/YeniSatis.fxml");
    }

    private void handleSatisGecmisi() {
        loadPage("/fxml/SatisGecmisi.fxml");
    }

    private void handleStokRaporu() {
        loadPage("/fxml/StokRaporu.fxml");
    }

    private void handleSatisRaporu() {
        loadPage("/fxml/SatisRaporu.fxml");
    }

    private void handleBorcRaporu() {
        loadPage("/fxml/BorcRaporu.fxml");
    }
}
