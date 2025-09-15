package com.veteriner;

import com.veteriner.controller.DashboardController;
import com.veteriner.controller.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Veteriner Stok Takip Sistemi");
        initRootLayout();
        showDashboardView();
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/DashboardView.fxml"));
            AnchorPane dashboardView = (AnchorPane) loader.load();
            rootLayout.setCenter(dashboardView);
            
            DashboardController controller = loader.getController();
            // controller.setMainApp(this); // Eğer controller'a MainApp referansı gerekirse
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showKategoriView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/KategoriView.fxml"));
            AnchorPane kategoriView = (AnchorPane) loader.load();
            rootLayout.setCenter(kategoriView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showUrunView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/UrunView.fxml"));
            AnchorPane urunView = (AnchorPane) loader.load();
            rootLayout.setCenter(urunView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMusteriView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/MusteriView.fxml"));
            AnchorPane musteriView = (AnchorPane) loader.load();
            rootLayout.setCenter(musteriView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSatisView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/SatisView.fxml"));
            AnchorPane satisView = (AnchorPane) loader.load();
            rootLayout.setCenter(satisView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
