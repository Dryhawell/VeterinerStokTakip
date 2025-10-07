package com.veteriner.controller;

import com.veteriner.model.Musteri;
import com.veteriner.model.Odeme;
import com.veteriner.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class MusteriController implements Initializable {
    private final ApiService apiService = ApiService.getInstance();
    private Musteri selectedMusteri;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML private TextField txtAd;
    @FXML private TextField txtSoyad;
    @FXML private TextField txtTelefon;
    @FXML private TextField txtEmail;
    @FXML private TextArea txtAdres;
    @FXML private TextField txtBorc;
    @FXML private TextField txtArama;
    @FXML private Label lblSeciliMusteri;
    @FXML private TextField txtOdemeTutar;
    @FXML private TextField txtOdemeAciklama;
    @FXML private Button btnOdemeAl;
    @FXML private Label lblStatus;

    @FXML private TableView<Musteri> musteriTable;
    @FXML private TableColumn<Musteri, Integer> colId;
    @FXML private TableColumn<Musteri, String> colAd;
    @FXML private TableColumn<Musteri, String> colSoyad;
    @FXML private TableColumn<Musteri, String> colTelefon;
    @FXML private TableColumn<Musteri, String> colEmail;
    @FXML private TableColumn<Musteri, Double> colBorc;
    @FXML private TableColumn<Musteri, Void> colIslemler;

    @FXML private TableView<Odeme> odemelerTable;
    @FXML private TableColumn<Odeme, String> colOdemeTarih;
    @FXML private TableColumn<Odeme, Double> colOdemeTutar;
    @FXML private TableColumn<Odeme, String> colOdemeAciklama;

    private ObservableList<Musteri> musteriler = FXCollections.observableArrayList();
    private ObservableList<Odeme> odemeler = FXCollections.observableArrayList();
    private FilteredList<Musteri> filteredMusteriler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupSearchFilter();
        loadMusteriler();
        setupTableSelection();
    }

    private void setupTableColumns() {
        // Müşteri tablosu
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colSoyad.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        colTelefon.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colBorc.setCellValueFactory(new PropertyValueFactory<>("borc"));
        
        // İşlemler sütunu için düzenle ve sil butonları bulunmakta
        colIslemler.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Düzenle");
            private final Button deleteButton = new Button("Sil");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Musteri musteri = getTableRow().getItem();
                    if (musteri != null) {
                        handleEdit(musteri);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Musteri musteri = getTableRow().getItem();
                    if (musteri != null) {
                        handleDelete(musteri);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        // Ödemeler tablosu bulunmaktadir
        colOdemeTarih.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getTarih().format(dateFormatter)
            )
        );
        colOdemeTutar.setCellValueFactory(new PropertyValueFactory<>("tutar"));
        colOdemeAciklama.setCellValueFactory(new PropertyValueFactory<>("aciklama"));
    }

    private void setupSearchFilter() {
        filteredMusteriler = new FilteredList<>(musteriler, p -> true);
        txtArama.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredMusteriler.setPredicate(createPredicate(newValue));
        });
        musteriTable.setItems(filteredMusteriler);
    }

    private Predicate<Musteri> createPredicate(String searchText) {
        return musteri -> {
            if (searchText == null || searchText.isEmpty()) return true;
            
            String lowerCaseFilter = searchText.toLowerCase();

            return musteri.getAd().toLowerCase().contains(lowerCaseFilter) ||
                   musteri.getSoyad().toLowerCase().contains(lowerCaseFilter) ||
                   musteri.getTelefon().toLowerCase().contains(lowerCaseFilter) ||
                   (musteri.getEmail() != null && musteri.getEmail().toLowerCase().contains(lowerCaseFilter));
        };
    }

    private void loadMusteriler() {
        apiService.getMusteriler()
            .thenAccept(list -> {
                musteriler.setAll(list);
                updateStatus("Müşteriler yüklendi");
            })
            .exceptionally(e -> {
                updateStatus("Hata: " + e.getMessage());
                return null;
            });
    }

    private void setupTableSelection() {
        musteriTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMusteri = newSelection;
                populateForm(newSelection);
                loadOdemeler(newSelection.getId());
                btnOdemeAl.setDisable(false);
                lblSeciliMusteri.setText(newSelection.getAd() + " " + newSelection.getSoyad());
            } else {
                btnOdemeAl.setDisable(true);
                lblSeciliMusteri.setText("");
            }
        });
    }

    private void populateForm(Musteri musteri) {
        txtAd.setText(musteri.getAd());
        txtSoyad.setText(musteri.getSoyad());
        txtTelefon.setText(musteri.getTelefon() != null ? musteri.getTelefon() : "");
        txtEmail.setText(musteri.getEmail() != null ? musteri.getEmail() : "");
        txtAdres.setText(musteri.getAdres() != null ? musteri.getAdres() : "");
        txtBorc.setText(String.valueOf(musteri.getBorc()));
    }

    private void loadOdemeler(Integer musteriId) {
        apiService.getOdemeler(musteriId)
            .thenAccept(list -> {
                odemeler.setAll(list);
                odemelerTable.setItems(odemeler);
            })
            .exceptionally(e -> {
                updateStatus("Ödemeler yüklenirken hata: " + e.getMessage());
                return null;
            });
    }

    private void clearForm() {
        txtAd.clear();
        txtSoyad.clear();
        txtTelefon.clear();
        txtEmail.clear();
        txtAdres.clear();
        txtBorc.clear();
        selectedMusteri = null;
        odemeler.clear();
        lblSeciliMusteri.setText("");
        btnOdemeAl.setDisable(true);
    }

    @FXML
    private void handleKaydet() {
        if (!validateForm()) {
            return;
        }

        Musteri musteri = new Musteri();
        musteri.setAd(txtAd.getText().trim());
        musteri.setSoyad(txtSoyad.getText().trim());
        musteri.setTelefon(txtTelefon.getText().trim());
        musteri.setEmail(txtEmail.getText().trim());
        musteri.setAdres(txtAdres.getText().trim());
        musteri.setBorc(0.0); // Yeni müşteri için borç sıfır olarak başlar ve gidişata göre birikir.

        if (selectedMusteri == null) {
            // Yeni müşteri ekleme işlemi eklenr
            apiService.createMusteri(musteri)
                .thenAccept(created -> {
                    musteriler.add(created);
                    clearForm();
                    updateStatus("Müşteri başarıyla eklendi");
                })
                .exceptionally(e -> {
                    updateStatus("Hata: " + e.getMessage());
                    return null;
                });
        } else {
            // Mevcut müşteriyi güncelleme işlemi bulunur.
            musteri.setId(selectedMusteri.getId());
            musteri.setBorc(selectedMusteri.getBorc()); // Mevcut borç durumunu korur
            apiService.updateMusteri(selectedMusteri.getId(), musteri)
                .thenAccept(updated -> {
                    int index = musteriler.indexOf(selectedMusteri);
                    musteriler.set(index, updated);
                    clearForm();
                    updateStatus("Müşteri başarıyla güncellendi");
                })
                .exceptionally(e -> {
                    updateStatus("Hata: " + e.getMessage());
                    return null;
                });
        }
    }

    @FXML
    private void handleIptal() {
        clearForm();
        musteriTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAra() {
        String aramaMetni = txtArama.getText().trim();
        filteredMusteriler.setPredicate(createPredicate(aramaMetni));
    }

    private void handleEdit(Musteri musteri) {
        selectedMusteri = musteri;
        populateForm(musteri);
    }

    private void handleDelete(Musteri musteri) {
        if (musteri.getBorc() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Silme İşlemi");
            alert.setHeaderText("Müşteri Silinemez");
            alert.setContentText("Bu müşterinin borcu bulunduğu için silinemez!");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Müşteri Silme");
        alert.setHeaderText("Müşteri Silme Onayı");
        alert.setContentText("Bu müşteriyi silmek istediğinizden emin misiniz?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                apiService.deleteMusteri(musteri.getId())
                    .thenAccept(success -> {
                        if (success) {
                            musteriler.remove(musteri);
                            clearForm();
                            updateStatus("Müşteri başarıyla silindi");
                        }
                    })
                    .exceptionally(e -> {
                        updateStatus("Hata: " + e.getMessage());
                        return null;
                    });
            }
        });
    }

    @FXML
    private void handleOdemeAl() {
        if (selectedMusteri == null) {
            updateStatus("Lütfen bir müşteri seçin!");
            return;
        }

        if (!validateOdeme()) {
            return;
        }

        double odemeTutari = Double.parseDouble(txtOdemeTutar.getText());
        String aciklama = txtOdemeAciklama.getText().trim();

        Odeme odeme = new Odeme();
        odeme.setTutar(odemeTutari);
        odeme.setAciklama(aciklama);
        odeme.setMusteriId(selectedMusteri.getId());

        apiService.createOdeme(odeme)
            .thenAccept(created -> {
                // Ödemeler listesini günceller
                odemeler.add(0, created); // En başa ekler

                // Müşteri borç bilgisini günceller
                selectedMusteri.setBorc(selectedMusteri.getBorc() - odemeTutari);
                int index = musteriler.indexOf(selectedMusteri);
                musteriler.set(index, selectedMusteri);
                
                // Formu temizler
                txtOdemeTutar.clear();
                txtOdemeAciklama.clear();
                txtBorc.setText(String.valueOf(selectedMusteri.getBorc()));
                
                updateStatus("Ödeme başarıyla kaydedildi");
            })
            .exceptionally(e -> {
                updateStatus("Hata: " + e.getMessage());
                return null;
            });
    }

    private boolean validateForm() {
        if (txtAd.getText().trim().isEmpty()) {
            updateStatus("Ad alanı boş olamaz!");
            return false;
        }

        if (txtSoyad.getText().trim().isEmpty()) {
            updateStatus("Soyad alanı boş olamaz!");
            return false;
        }

        if (!txtTelefon.getText().trim().isEmpty() && !txtTelefon.getText().trim().matches("\\d{10,11}")) {
            updateStatus("Geçerli bir telefon numarası giriniz!");
            return false;
        }

        if (!txtEmail.getText().trim().isEmpty() && !txtEmail.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            updateStatus("Geçerli bir e-posta adresi giriniz!");
            return false;
        }

        return true;
    }

    private boolean validateOdeme() {
        if (txtOdemeTutar.getText().trim().isEmpty()) {
            updateStatus("Ödeme tutarını giriniz!");
            return false;
        }

        try {
            double tutar = Double.parseDouble(txtOdemeTutar.getText());
            if (tutar <= 0) {
                updateStatus("Ödeme tutarı sıfırdan büyük olmalıdır!");
                return false;
            }
            if (tutar > selectedMusteri.getBorc()) {
                updateStatus("Ödeme tutarı borç tutarından büyük olamaz!");
                return false;
            }
        } catch (NumberFormatException e) {
            updateStatus("Geçerli bir ödeme tutarı giriniz!");
            return false;
        }

        return true;
    }

    private void updateStatus(String message) {
        if (lblStatus != null) {
            lblStatus.setText(message);
        }
    }
}