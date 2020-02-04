/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.poshpaws.appscheduler.cache.PetCache;
import com.poshpaws.appscheduler.dao.DBConnection;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import com.poshpaws.appscheduler.model.User;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
public class CustomerPaneController {

    // Controllers
//    private AppointmentScreenController mainController;
    private jCalendar mainApp;
    private User currentUser;
    private Appointment selectedAppt;
    @FXML
    private Label topLabel;

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
    private JFXTextField txtNewPet;

    @FXML
    private JFXTextField txtPetDesc;

    @FXML
    private JFXTextField txtPetType;

    @FXML
    private JFXTextField txtNotes;

    @FXML
    private JFXComboBox<Pet> cbPetSelection;

    @FXML
    private JFXButton btnCustSave;

    @FXML
    private JFXButton btnCustDel;
    @FXML
    private JFXButton btnPetSave;
    @FXML
    private JFXButton btnPetDel;
    @FXML
    private JFXButton btnUpload;
    @FXML
    private Label petLabel;

    private Pet selectedPet;
    private String savedUser;
//    private Customer selCustomer;
//    private ObservableList<Pet> pets = FXCollections.observableArrayList();

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

//    Customer customer;
    /**
     * Initializes the controller class.
     *
     * @param mainApp
     * @param currentUser
     */
    public void setMainController(jCalendar mainApp, User currentUser) {

        this.mainApp = mainApp;
        this.currentUser = currentUser;

        //get user ID
        String userName = "";
        Preferences userPreferences = Preferences.userRoot();
        savedUser = userPreferences.get("username", userName);

//        ObservableList<Pet> pets = PetCache.getSelectedPets(c);
        //Populate pet type and description based on selected pet
        cbPetSelection.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                //set selected pet
                selectedPet = cbPetSelection.getSelectionModel().getSelectedItem();
                petSelected(selectedPet);

                imageRetrievalService.restart();
            }
        });

//        selectedPet = c.getPet();
//        Pet pet = PetCache.getPet(appt.getPet().getPetId());
//        loadPetPhoto(appt.getPet());
//        imageRetrievalService.restart();
        petPhoto.imageProperty().bind(imageRetrievalService.valueProperty());

        convertComboBoxString();
//        //CLOSE PANE ON EXIT
//        System.out.println("running initialize()");
        btnClose.setOnMouseClicked((evt) -> {
            mainApp.closeBottomBorder();
        });

    }

    @FXML
    private void showCustomerDetails(Customer c) {

//        selCustomer = c;
//        apptIdLabel.setText(c.getAppointmentId());
//        String startTime = DateTimeUtil.parseTimeToStringFormat(c.getStart().toLocalTime());
        txtCustomerName.setText(c.getCustomerName());
        txtCustomerPhone.setText(c.getPhone());
        txtCustomerEmail.setText(c.getEmail());
        txtNotes.setText(c.getNotes());

        //Add New Pet to dropdown list
//        ObservableList<Pet> pets = PetCache.getSelectedPets(c);
//        cbPetSelection.getItems().clear();
//        cbPetSelection.setItems(pets);
//        cbPetSelection.getSelectionModel().selectFirst();
//        pets.addAll(PetCache.getSelectedPets(c));
//        Pet newPet = new Pet("-1", "New Pet", "x", "x");
//        pets.add(newPet);
//        cbPetSelection.setItems(pets);
    }

    private static boolean isEmpty(ImageView imageView) {
        Image image = imageView.getImage();
        return image == null || image.isError();
    }

    private boolean hasPetPhoto() {
        Pet p = cbPetSelection.getSelectionModel().getSelectedItem();
        String query = "SELECT image FROM images WHERE petId = ?";

        try {
            PreparedStatement ps = DBConnection.getConn().prepareStatement(query);
            ps.setString(1, p.getPetId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                InputStream is = rs.getBinaryStream("image");

                //check image size, maybe not hard coded,
//                maybe store image size in db
                Image image = new Image(is);
//                Image image = new Image(is, 250, 300, false, true);

//                petPhoto.setImage(image);
                petLabel.setText(p.getPetName() + "'s Photo!");
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        petPhoto.setImage(null);
        petLabel.setText("No photo available");
        return false;
    }

//    @FXML
//    private void showCustomerDetails(Appointment appt) {
//
//        apptIdLabel.setText(appt.getAppointmentId());
//        String startTime = DateTimeUtil.parseTimeToStringFormat(appt.getStart().toLocalTime());
//
//        txtCustomerName.setText(appt.getCustomer().getCustomerName());
//        txtCustomerPhone.setText(appt.getCustomer().getPhone());
//        txtCustomerEmail.setText(appt.getCustomer().getEmail());
//        txtNotes.setText(appt.getCustomer().getNotes());
//        txtNewPet.setText(appt.getPet().getPetName());
//        txtPetDesc.setText(appt.getPet().getPetDesc());
//        txtPetType.setText(appt.getPet().getPetType());
//        selectedPet = appt.getPet();
////        Pet pet = PetCache.getPet(appt.getPet().getPetId());
////        loadPetPhoto(appt.getPet());
//
//        imageRetrievalService.restart();
//
//        petPhoto.imageProperty().bind(imageRetrievalService.valueProperty());
////can't center image photo
//// requires getting a pet id somewhere
//    }
    @FXML
    void handleApptCancel(ActionEvent event) {
        mainApp.closeBottomBorder();
    }

//    public void setSelectedAppointment(Appointment appt) {
//        //initialize update fields here
//        topLabel.setText("Edit Appointment Details");
////        choiceNewCustomer.setDisable(true);
//
////        showCustomerDetails(appt);
//    }
    public void setSelected(Customer c) {
        //initialize update fields here
        topLabel.setText("Editing Customer Details");
//        choiceNewCustomer.setDisable(true);

        showCustomerDetails(c);

        ObservableList<Pet> pets = PetCache.getSelectedPets(c);
        Pet newPet = new Pet("-1", "New Pet", "x", "x");
        pets.add(newPet);
        cbPetSelection.setItems(pets);

    }

    public Image getImageById(String id) throws SQLException, IOException {

        try (
                PreparedStatement ps = DBConnection.getConn().prepareStatement(
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
    @FXML
    public void initialize() {

    }

    private void petSelected(Pet p) {

        //hide pet name field if New Pet is selected
        if (p.getPetId().equals("-1")) {
//            cbPetSelection.getSelectionModel().selectFirst();
            txtNewPet.clear();
//            selectPetLabel.setVisible(true);
//            txtPetName.setVisible(true);
            txtPetDesc.clear();
        } else {
//            selectPetLabel.setVisible(false);
//            txtPetName.setVisible(false);
//            txtPetName.setText(p.getPetName());
//            comboPetType.setValue(p.getPetType());
//            txtPetDescription.setText(p.getPetDesc());
            txtNewPet.setText(p.getPetName());
            txtPetDesc.setText(p.getPetDesc());
            txtPetType.setText(p.getPetType());

            //Add New Pet to dropdown list
//            selectedPet = p;
        }

    }

    @FXML
    void handleUploadPhoto(ActionEvent actionEvent) {

        System.out.println("is pet image empty ?" + isEmpty(petPhoto));
        Pet p = cbPetSelection.getSelectionModel().getSelectedItem();

        boolean uploadSuccess = false;

        if (p.getPetId().isEmpty()) {
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

                petPhoto.setImage(image);

                try {

                    if (isEmpty(petPhoto)) {
                        //insert new photo
                        PreparedStatement ps = DBConnection.getConn().prepareStatement("INSERT INTO images "
                                + "(petId, image, name, createDate, createdBy, lastUpdate, lastUpdatedBy) "
                                + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?) ");

                        FileInputStream fs = new FileInputStream(imgPath);
                        ps.setString(1, p.getPetId());
                        ps.setBinaryStream(2, fs);
                        ps.setString(3, p.getPetName());
                        ps.setString(4, savedUser);
                        ps.setString(5, savedUser);

                        if (ps.executeUpdate() == 1) {
                            uploadSuccess = true;
                            System.out.println("Added new photo " + uploadSuccess);
                        }

                    } else {

                        //update photo
                        PreparedStatement ps = DBConnection.getConn().prepareStatement("UPDATE images SET image = ?, name = ?, "
                                + "lastUpdate = CURRENT_TIMESTAMP, lastUpdatedBy = ? "
                                + "WHERE petId = ?");
                        FileInputStream fs = new FileInputStream(imgPath);
                        ps.setBinaryStream(1, fs);
                        ps.setString(2, p.getPetName());
                        ps.setString(3, savedUser);

                        ps.setString(4, p.getPetId());

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
        petPhoto.imageProperty().bind(imageRetrievalService.valueProperty());

    }

    private void convertComboBoxString() {

        cbPetSelection.setConverter(new StringConverter<Pet>() {
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
                return cbPetSelection.getItems().stream().filter(ap -> ap.nameProperty().get().equals(string)).findFirst().orElse(null);
            }

        }
        );

    }
}
