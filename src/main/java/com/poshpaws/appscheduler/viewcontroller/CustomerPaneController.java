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
import com.poshpaws.appscheduler.cache.CustomerCache;
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
import java.net.URL;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.ButtonType;
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
    private JFXTextField txtPetName;

    @FXML
    private JFXTextField txtPetDescription;

    @FXML
    private JFXTextField txtPetType;

    @FXML
    private JFXTextField txtCustomerNotes;

    @FXML
    private JFXComboBox<Pet> cbPetSelection;

    @FXML
    private JFXComboBox<String> comboPetType;
    @FXML
    private JFXButton btnCustomerSave;

    @FXML
    private JFXButton btnCustomerCancelSave;
    @FXML
    private JFXButton btnPetSave;
    @FXML
    private JFXButton btnPetDelete;
    @FXML
    private JFXButton btnUpload;
    @FXML
    private Label petLabel;
    @FXML
    private Label custLabel;
    @FXML
    private JFXCheckBox checkboxActive;
    @FXML
    private JFXButton btnSaveNew;

    private Pet selectedPet;
    private Customer selectedCustomer;
    private String savedUser;
    private boolean editMode;
//    private Customer selCustomer;
//    private ObservableList<Pet> pets = FXCollections.observableArrayList();

    public SimpleStringProperty textToDisplay = new SimpleStringProperty("");

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

//    @FXML
//    void handleAddCustomer(ActionEvent event) {
//
//        editMode = false;
//        showButtons();
//
//        //Add mode, disable other buttons except Save and Cancel
//        topLabel.setText("Add New Customer");
//        checkboxActive.setSelected(true);
//        cbPetSelection.setVisible(false);
//        txtCustomerName.requestFocus();
//
//        clearFields();
//        enableEdits();
//
//    }
    @FXML
    void handleDeletePet(ActionEvent event) {
        Pet p = cbPetSelection.getSelectionModel().getSelectedItem();

        if (p.getPetId().equals("-1") || p.getPetId().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Can't delete ");
            alert.setHeaderText("Please select an existing pet to delete");
//            alert.setContentText("Please select a customer to delete");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm delete");
            alert.setHeaderText("Are you sure you want to delete " + p.getPetName() + " ?");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        deletePet(p);

                        PetCache.flush();
                        CustomerCache.flush();
//                        loadTableView();
//                        setDefault();
                    });

        }
    }

    @FXML
    void handleSaveCustomer(ActionEvent event) {

//        Customer selectedCustomer = CustomerTable.getSelectionModel().getSelectedItem();
        System.out.println("Debug handleSaveCustomer: editMode: " + editMode);
        if (validateCustomer()) {
            if (editMode) {
                //in editmode, show four buttons
                updateCustomer(selectedCustomer);
            } else {
                //in add mode, hide delete buttons
                //only show one save button
                //saves both customer and pet
                saveNewCustomer();
            }
            PetCache.flush();
            CustomerCache.flush();
//            loadTableView();
//            setDefault();

        }
    }

    @FXML
    void handleSavePet(ActionEvent event) {

        Pet p = cbPetSelection.getSelectionModel().getSelectedItem();
        System.out.println("Save Pet button clicked. Edit mode + " + editMode);
        if (validatePet()) {
            if (p.getPetId().equals("-1")) {
                addNewPet();
            } else {
                updatePet(p);

            }
            PetCache.flush();

        }
    }

    private void showButtons() {
        btnCustomerCancelSave.setVisible(true);
        btnCustomerSave.setVisible(true);
        btnPetSave.setVisible(true);
        btnPetDelete.setVisible(true);
    }

    private void hideButtons() {

        btnCustomerCancelSave.setVisible(false);
        btnCustomerSave.setVisible(false);
        btnPetSave.setVisible(false);
        btnPetDelete.setVisible(false);
    }

    private void enableEdits() {

        txtCustomerName.setEditable(true);
        txtCustomerPhone.setEditable(true);
        txtCustomerEmail.setEditable(true);
        txtCustomerNotes.setEditable(true);
        txtPetName.setEditable(true);
        comboPetType.setEditable(true);
        txtPetDescription.setEditable(true);
    }

    private void disableEdits() {

        txtCustomerName.setEditable(false);
        txtCustomerPhone.setEditable(false);
        txtCustomerEmail.setEditable(false);
        txtCustomerNotes.setEditable(false);;
        txtPetName.setEditable(false);
        comboPetType.setEditable(false);
        txtPetDescription.setEditable(false);
    }

    private void clearFields() {
//        labelCusID.setText("");
        txtCustomerName.clear();
        txtCustomerPhone.clear();
        txtCustomerEmail.clear();
        txtCustomerNotes.clear();
        txtPetName.clear();
        txtPetDescription.clear();
    }

    private void deletePet(Pet p) {

        try {
            PreparedStatement pst = DBConnection.getConn().prepareStatement("DELETE FROM pet, images WHERE pet.petId = ?");
            pst.setString(1, p.getPetId());
            pst.executeUpdate();

            System.out.println("Deleted pet " + p.getPetId() + " - " + p.getPetName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts new customer record
     */
    private void saveNewCustomer() {
        System.out.println("Save new customer and pet..");
        //add validation to check new pet fields are filled
        try {

            PreparedStatement pst = DBConnection.getConn().prepareStatement("INSERT INTO customer "
                    + "( customerName, customerPhone, customerEmail, notes, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + " VALUES ( ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, txtCustomerName.getText());
            pst.setString(2, txtCustomerPhone.getText());
            pst.setString(3, txtCustomerEmail.getText());
            pst.setString(4, txtCustomerNotes.getText());

            if (checkboxActive.isSelected()) {
                pst.setInt(5, 1);
            } else {
                pst.setInt(5, 0);
            }

            pst.setString(6, savedUser);
            pst.setString(7, savedUser);

            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();

            int newPetId = -1;
            int newCustomerId = -1;

            if (rs.next()) {
                newPetId = rs.getInt(1);
                newCustomerId = rs.getInt(1);
            }

            PreparedStatement ps = DBConnection.getConn().prepareStatement("INSERT INTO pet "
                    + "(petName, petType, petDescription, createDate, createdBy, lastUpdate, lastUpdateBy, customerId) "
                    + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?)");
            ps.setString(1, txtPetName.getText());
            ps.setString(2, comboPetType.getValue());
            ps.setString(3, txtPetDescription.getText());
            ps.setString(4, savedUser);
            ps.setString(5, savedUser);
            ps.setInt(6, newCustomerId);

            ps.executeUpdate();

            System.out.println("Attempting to save new record.. "
                    + "New Customer ID" + newCustomerId + "\n"
                    + "Name: " + txtCustomerName.getText() + "\n"
                    + "Phone: " + txtCustomerPhone.getText() + "\n"
                    + "Email: " + txtCustomerEmail.getText() + "\n"
                    + "Notes: " + txtCustomerNotes.getText() + "\n"
                    + "Status: " + checkboxActive.isSelected() + "\n"
                    + "Pet: " + txtPetName.getText() + "\n"
                    + "Pet Type: " + comboPetType.getValue() + "\n"
                    + "Pet Notes: " + txtPetDescription.getText() + "\n"
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates customer records
     */
    private void updateCustomer(Customer c) {
        System.out.println("Updating current customer..");

        try {

            PreparedStatement pst = DBConnection.getConn().prepareStatement("UPDATE customer "
                    + "SET customerName= ?, customerPhone = ?, customerEmail=?, notes=?, active=?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
                    + "WHERE customerId = ? ");
            pst.setString(1, txtCustomerName.getText());
            pst.setString(2, txtCustomerPhone.getText());
            pst.setString(3, txtCustomerEmail.getText());
            pst.setString(4, txtCustomerNotes.getText());

            if (checkboxActive.isSelected()) {
                pst.setInt(5, 1);
            } else {
                pst.setInt(5, 0);
            }
            pst.setString(6, savedUser);
            pst.setString(7, c.getCustomerId());

            pst.executeUpdate();
            //            System.out.println("Attempting to save new record.. "
            //                    + "Existing Customer ID" + c.getCustomerId() + "\n"
            //                    + "Name: " + txtCustomerName.getText() + "\n"
            //                    + "Phone: " + txtCustomerPhone.getText() + "\n"
            //                    + "Email: " + txtCustomerEmail.getText() + "\n"
            //                    + "Notes: " + txtCustomerNotes.getText() + "\n"
            //                    + "Status: " + checkboxActive.isSelected() + "\n"

//            );
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

//		ps = DBConnection.getConn().prepareStatement(insertCountryStatement);
//		ps.setString(1, country);
//		ps.setString(2, "CURRENT_TIMESTAMP");
//		ps.setString(3, currentUser.getUserName());
//		ps.setString(4, "CURRENT_TIMESTAMP");
//		ps.setString(5, currentUser.getUserName());
//		ps.executeUpdate();
//
//		countryId = getCountryId(country, currentUser);
//	    }
//	} catch (SQLException e) {
//	    e.printStackTrace();
//	}
//	return countryId;
//    }
    }

    private void addNewPet() {
        System.out.println("Update to save new pet");

        try {
//            Customer c = CustomerTable.getSelectionModel().getSelectedItem();
            String selectedCustomerId = selectedPet.getCustomerId();

            PreparedStatement ps = DBConnection.getConn().prepareStatement("INSERT INTO pet "
                    + "(petName, petType, petDescription, createDate, createdBy, lastUpdate, lastUpdateBy, customerId) "
                    + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?)");
            ps.setString(1, txtPetName.getText());
            ps.setString(2, comboPetType.getValue());
            ps.setString(3, txtPetDescription.getText());
            ps.setString(4, savedUser);
            ps.setString(5, savedUser);
            ps.setString(6, selectedCustomerId);

            ps.executeUpdate();

            System.out.println("Attempting to save new pet.. "
                    + "Customer" + selectedCustomer.getCustomerName() + "\n"
                    + "Pet: " + txtPetName.getText() + "\n"
                    + "Pet Type: " + comboPetType.getValue() + "\n"
                    + "Pet Notes: " + txtPetDescription.getText() + "\n"
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates pet record
     */
    private void updatePet(Pet p) {
        System.out.println("Update pet for current customer");

        try {

            PreparedStatement pst = DBConnection.getConn().prepareStatement("UPDATE pet "
                    + "SET petName=?, petType=?, petDescription=?, lastUpdate=CURRENT_TIMESTAMP, lastUpdateBy=? "
                    + "WHERE petId = ? ");
            pst.setString(1, txtPetName.getText());
            pst.setString(2, comboPetType.getValue());
            pst.setString(3, txtPetDescription.getText());
            pst.setString(4, savedUser);
            pst.setString(5, p.getPetId());

            pst.executeUpdate();

            System.out.println("Attempting to update pet.. "
                    + "Existing ID" + p.getPetId() + "\n"
                    + "Pet: " + txtPetName.getText() + "\n"
                    + "Pet Type: " + comboPetType.getValue() + "\n"
                    + "Pet Notes: " + txtPetDescription.getText() + "\n"
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateCustomer() {

        //EXCEPTION CONTROL: Validate nonexistent or invalid customer data
        String name = txtCustomerName.getText();
        String phone = txtCustomerPhone.getText();
        String email = txtCustomerEmail.getText();
        //notes are optional
//        String notes = txtCustomerNotes.getText();
        String petName = txtPetName.getText();
        String petType = comboPetType.getValue();
        //pet desc optional
//        String petDesc = txtPetDescription.getText();

        String errorMessage = "";

        if (name == null || name.length() == 0) {
            errorMessage += "Please enter customer name.\n";
        }

        if (phone == null || phone.length() == 0) {
            errorMessage += "Please enter a phone number).";
        } else if (phone.length() < 10 || phone.length() > 20) {
            errorMessage += "Phone number must be between 10 and 20 digits.\n";
        }

        if (email == null || email.length() == 0) {
            errorMessage += "Please enter an email address.\n";
        }

        if (petName == null || petName.length() == 0) {
            errorMessage += "Please select a pet name.\n";
        }
        if (petType == null || petType.length() == 0) {
            errorMessage += "Please select pet type.\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid data");
            alert.setHeaderText("Please fix the following customer field(s)");
            alert.setContentText(errorMessage);

            alert.showAndWait();
            System.out.println("Validating "
                    + "Name: " + txtCustomerName.getText() + "\n"
                    + "Phone: " + txtCustomerPhone.getText() + "\n"
                    + "Email: " + txtCustomerEmail.getText() + "\n"
                    + "Pet: " + petName + "\n"
                    + "Pet Type: " + comboPetType.getValue() + "\n"
            );

            return false;
        }
    }

    private boolean validatePet() {

        //EXCEPTION CONTROL: Validate nonexistent or invalid customer data
        String petName = txtPetName.getText();
        String petType = comboPetType.getValue();
        //pet desc optional
//        String petDesc = txtPetDescription.getText();

        String errorMessage = "";

        if (petName == null || petName.length() == 0) {
            errorMessage += "Please select a pet name.\n";
        }
        if (petType == null || petType.length() == 0) {
            errorMessage += "Please select pet type.\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid data");
            alert.setHeaderText("Please fix the following pet field(s)");
            alert.setContentText(errorMessage);

            alert.showAndWait();
            System.out.println("Validating "
                    + "Pet: " + petName + "\n"
                    + "Pet Type: " + comboPetType.getValue() + "\n"
            );

            return false;
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param mainApp
     * @param currentUser
     */
    public void setMainController(jCalendar mainApp, User currentUser) {

        this.mainApp = mainApp;
        this.currentUser = currentUser;

        //
//        editMode = false;
//        showButtons();
//
//        //Add mode, disable other buttons except Save and Cancel
        topLabel.setText("Add New Customer");
        selectedCustomer = null;
        checkboxActive.setSelected(true);
        hideButtons();
        btnSaveNew.setVisible(true);
//        cbPetSelection.setVisible(false);
        txtCustomerName.requestFocus();
//
//        clearFields();
//        enableEdits();

        comboPetType.getItems().clear();
        comboPetType.setItems(Pet.getPetTypes());

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
        txtCustomerNotes.setText(c.getNotes());

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

    @FXML
    void handleApptCancel(ActionEvent event) {
        mainApp.closeBottomBorder();
    }

    public void setSelected(Customer c) {
        //initialize update fields here
        topLabel.setText("Editing Customer Details");
        selectedCustomer = c;

        showCustomerDetails(c);

        ObservableList<Pet> pets = PetCache.getSelectedPets(c);
        Pet newPet = new Pet("-1", "New Pet", "x", "x");
        pets.add(newPet);
        cbPetSelection.setItems(pets);
        cbPetSelection.getSelectionModel().selectFirst();
        petLabel.setText(selectedCustomer.getCustomerName());
        showButtons();
        btnSaveNew.setVisible(false);
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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        custLabel.textProperty().bind(textToDisplay);
    }

    private void petSelected(Pet p) {

        //hide pet name field if New Pet is selected
        if (p.getPetId().equals("-1")) {
//            cbPetSelection.getSelectionModel().selectFirst();
            txtPetName.clear();
//            selectPetLabel.setVisible(true);
//            txtPetName.setVisible(true);
            txtPetDescription.clear();
            comboPetType.getSelectionModel().selectFirst();
        } else {
//            selectPetLabel.setVisible(false);
//            txtPetName.setVisible(false);
//            txtPetName.setText(p.getPetName());
//            comboPetTypeType.setValue(p.getPetType());
//            txtPetDescription.setText(p.getPetDesc());
            txtPetName.setText(p.getPetName());
            txtPetDescription.setText(p.getPetDesc());
//            txtPetType.setText(p.getPetType());
            comboPetType.setValue(p.getPetType());

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
