module com.veteriner {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires retrofit2;
    requires com.google.gson;
    requires java.sql;
    requires retrofit2.converter.gson;

    opens com.veteriner to javafx.fxml, java.base;
    opens com.veteriner.controller to javafx.fxml;
    opens com.veteriner.model to com.google.gson, java.base;
    
    exports com.veteriner;
    exports com.veteriner.controller;
    exports com.veteriner.model;
    exports com.veteriner.service;
}
