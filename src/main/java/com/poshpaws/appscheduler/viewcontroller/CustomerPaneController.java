/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.poshpaws.appscheduler.cache.PetCache;
import com.poshpaws.appscheduler.dao.DBHandler;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Jen
 */
public class CustomerPaneController implements Initializable {

    private jCalendar mainApp;

    @FXML
    private ImageView btnClose;

    @FXML
    private Label apptIdLabel;

    @FXML
    private ImageView petPhoto;

    @FXML
    private JFXTextField txtCustomerName;

    @FXML
    private JFXTextField txtCustomerPhone;

    @FXML
    private JFXTextField txtCustomerEmail;

    @FXML
    private JFXTextField txtNotes;

    @FXML
    private JFXTextField txtPetName;

    @FXML
    private JFXTextField txtPetDescription;

    @FXML
    private JFXTextField txtPetType;
    @FXML
    private JFXComboBox<Pet> cbPets;
    @FXML
    private JFXButton btnUpload;
    @FXML
    private JFXCheckBox checkboxActive;
    private Pet selectedPet;
    private Customer selectedCustomer;
    private String savedUser;
    private Pet selected;

    public SimpleStringProperty textToDisplay = new SimpleStringProperty("");

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

    /**
     * Initializes the controller class.
     *
     * @param mainApp
     * @param currentUser
     */
    public void setMainController(jCalendar mainApp) {

        this.mainApp = mainApp;

        //get user ID
        String userName = "";
        Preferences userPreferences = Preferences.userRoot();
        savedUser = userPreferences.get("username", userName);

        cbPets.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                selectedPet = cbPets.getSelectionModel().getSelectedItem();
                petSelected(selectedPet);
                imageRetrievalService.restart();
            }
        });

        petPhoto.imageProperty().bind(imageRetrievalService.valueProperty());
        convertComboBoxString();
        btnClose.setOnMouseClicked((evt) -> {
            mainApp.closeRightBorder();
        });

    }

    @FXML
    private void showCustomerDetails(Customer c) {

        txtCustomerName.setText(c.getCustomerName());
        txtCustomerPhone.setText(c.getPhone());
        txtCustomerEmail.setText(c.getEmail());
        txtNotes.setText(c.getNotes());
        checkboxActive.setSelected(c.getActive());

    }

    private static boolean isEmpty(ImageView imageView) {
        Image image = imageView.getImage();
        return image == null || image.isError();
    }

    @FXML
    void handleApptCancel(ActionEvent event) {
        mainApp.closeRightBorder();
    }

    public void setSelected(Customer c) {
        selectedCustomer = c;
        showCustomerDetails(c);
        cbPets.getSelectionModel().clearSelection();

        ObservableList<Pet> pets = PetCache.getSelectedPets(c);
        cbPets.setItems(pets);
        cbPets.getSelectionModel().selectFirst();

    }

    public Image getImageById(String id) throws SQLException, IOException {

        try (
                PreparedStatement ps = DBHandler.getConn().prepareStatement(
                        "SELECT image FROM images WHERE petId = ?");) {

            ps.setString(1, id);
            ResultSet results = ps.executeQuery();
            Image img = null;
            if (results.next()) {
                Blob foto = results.getBlob("image");
                InputStream is = foto.getBinaryStream();
                img = new Image(is);
                is.close();
            }
            results.close();
            return img;
        }
    }

    private final Service<Image> imageRetrievalService = new Service<Image>() {
        @Override
        protected Task<Image> createTask() {
            final String id;

            if (selectedPet == null) {
                id = null;
            } else {
                id = selectedPet.getPetId();
            }
            return new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    if (id == null) {
                        return null;
                    }
                    return getImageById(id);
                }
            };
        }
    };

    //constructor is called first, The initialize method is called after all @FXML annotated members have been injected
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void petSelected(Pet p) {

        txtPetName.setText(p.getPetName());
        txtPetDescription.setText(p.getPetDesc());
        txtPetType.setText(p.getPetType());

    }

    @FXML
    void handleUploadPhoto(ActionEvent actionEvent) {

//        System.out.println("is pet image empty ?" + isEmpty(petPhoto));
        selectedPet = cbPets.getSelectionModel().getSelectedItem();

        boolean uploadSuccess = false;

        if (selectedPet.getPetId().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No pet selected");
            alert.setHeaderText("Please select a pet first");

            alert.showAndWait();
        } else {

            petPhoto.imageProperty().unbind();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("*.Images", "*.bmp", "*.png", "*.jpg", "*.gif"));

            File imgPath = fileChooser.showOpenDialog(null);
            if (imgPath != null) {
                Image image = new Image(imgPath.toURI().toString());

                try {

                    if (isEmpty(petPhoto)) {
                        petPhoto.setImage(image);
                        //insert new photo
                        PreparedStatement ps = DBHandler.getConn().prepareStatement("INSERT INTO images "
                                + "(petId, image, name, createDate, createdBy, lastUpdate, lastUpdatedBy) "
                                + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?) ");

                        FileInputStream fs = new FileInputStream(imgPath);
                        ps.setString(1, selectedPet.getPetId());
                        ps.setBinaryStream(2, fs);
                        ps.setString(3, selectedPet.getPetName());
                        ps.setString(4, savedUser);
                        ps.setString(5, savedUser);

                        if (ps.executeUpdate() == 1) {
                            uploadSuccess = true;
                            System.out.println("Added new photo " + uploadSuccess);
                        }

                    } else {

                        //update photo
                        petPhoto.setImage(image);
                        PreparedStatement ps = DBHandler.getConn().prepareStatement("UPDATE images SET image = ?, name = ?, "
                                + "lastUpdate = CURRENT_TIMESTAMP, lastUpdatedBy = ? "
                                + "WHERE petId = ?");
                        FileInputStream fs = new FileInputStream(imgPath);
                        ps.setBinaryStream(1, fs);
                        ps.setString(2, selectedPet.getPetName());
                        ps.setString(3, savedUser);

                        ps.setString(4, selectedPet.getPetId());

                        if (ps.executeUpdate() == 1) {
                            System.out.println("Updated photo " + uploadSuccess);
                        }

                    }

                    imgPath = null;
                } catch (Exception ex) {

                    Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
        imageRetrievalService.restart();
        petPhoto.imageProperty().bind(imageRetrievalService.valueProperty());
    }

    private void convertComboBoxString() {

        cbPets.setConverter(new StringConverter<Pet>() {
            @Override
            public String toString(Pet object) {
                if (object == null) {
                    return null;
                } else {
                    return object.nameProperty().get();
                }
            }

            @Override
            public Pet fromString(String string) {
                return cbPets.getItems().stream().filter(ap -> ap.nameProperty().get().equals(string)).findFirst().orElse(null);
            }

        }
        );

    }
}
