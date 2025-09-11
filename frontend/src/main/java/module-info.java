module com.veteriner {
    requires javafx.controls;
    requires javafx.fxml;
    requires retrofit2;
    requires com.google.gson;
    requires java.sql;
    requires retrofit2.converter.gson;
    requires com.itextpdf;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.veteriner to javafx.fxml;
    opens com.veteriner.controller to javafx.fxml;
    opens com.veteriner.model to com.google.gson;
    
    exports com.veteriner;
    exports com.veteriner.controller;
    exports com.veteriner.model;
    exports com.veteriner.service;
}
