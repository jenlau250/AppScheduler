/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.poshpaws.appscheduler.cache.CustomerCache;
import com.poshpaws.appscheduler.cache.PetCache;
import com.poshpaws.appscheduler.dao.DBConnection;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import com.poshpaws.appscheduler.model.User;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author jlau2
 */
public class CustomerScreenController {

    private jCalendar mainApp;
    private User currentUser;
    private Customer customer;

    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private Label customerLabel;
    @FXML
    private Label labelCusID;
    @FXML
    private Label petLabel;
    @FXML
    private JFXButton btnCustomerAdd;
    @FXML
    private JFXButton btnCustomerUpdate;
    @FXML
    private JFXButton btnCustomerDelete;
    @FXML
    private JFXButton btnCustomerSave;
    @FXML
    private JFXButton btnCustomerCancelSave;
    @FXML
    private TableView<Customer> CustomerTable;
    @FXML
    private TableColumn<Customer, String> colCustomerID;
    @FXML
    private TableColumn<Customer, String> colCustomerName;
    @FXML
    private TableColumn<Customer, String> colCustomerPhone;

    @FXML
    private TableColumn<Customer, String> colCustomerEmail;
    @FXML
    private TableColumn<Customer, Boolean> colCustomerStatus;

    @FXML
    private JFXTextField txtCustomerName;
    @FXML
    private JFXTextField txtCustomerPhone; //Address
    @FXML
    private JFXTextField txtCustomerEmail; //Address2

    @FXML
    private JFXCheckBox checkboxActive;
//    private JFXTextField txtCustomerStatus; //ZipCode
    @FXML
    private JFXTextField txtCustomerNotes; //Phone

    @FXML
    private JFXComboBox<Pet> cbPetSelection;
    @FXML
    private JFXTextField txtPetName;

    @FXML
    private JFXComboBox<String> comboPetType;

    @FXML
    private JFXTextField filterCustomer;

    @FXML
    private JFXComboBox filterStatus;

    @FXML
    private JFXButton btnPetSave;

    @FXML
    private JFXButton btnPetDelete;

    @FXML
    private TextField txtPetDescription;

    @FXML
    private ImageView petPhoto;

    @FXML
    private JFXButton btnUpload;

    @FXML
    private Label selectPetLabel;

    String imgPath = null;

    private String savedUser;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private ObservableList<Pet> petList = FXCollections.observableArrayList(); //Cities
    private ObservableList<Pet> customerPets = FXCollections.observableArrayList(); //selectedCountries
    private ObservableList<Pet> selectedPets = FXCollections.observableArrayList(); //selectedCountries
    private static ObservableList<String> petNames = FXCollections.observableArrayList(); //selectedCountries
    private ObservableList<Customer> selectedStatus = FXCollections.observableArrayList();

    private boolean editMode;

    private String petImageFile;

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

    public CustomerScreenController() {

    }
//
//    @FXML
//    private void initializePetTypes() {
//
//    }

    @FXML
    private void initializeStatusCombo() {

        filterStatus.getItems().addAll("All", "Active", "Inactive");

    }

    @FXML
    void handleAddCustomer(ActionEvent event) {

//        mainApp.showCustomerPane();
        editMode = false;
        CustomerTable.setDisable(true);
        showButtons();

        //Add mode, disable other buttons except Save and Cancel
        customerLabel.setText("Add New Customer");
        labelCusID.setText("Auto Generated");
        btnCustomerAdd.setDisable(true);
        btnCustomerUpdate.setDisable(true);
        btnCustomerDelete.setDisable(true);
        checkboxActive.setSelected(true);

        cbPetSelection.setVisible(false);

        CustomerTable.setDisable(true);

        txtCustomerName.requestFocus();

        clearFields();
        enableEdits();
    }

    @FXML
    void handleCancelCustomer(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm cancel");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.showAndWait()
                // Lambda use - set OK button to respond
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {

                    setDefault();
                });
    }

    @FXML
    void handleDeleteCustomer(ActionEvent event) {

        Customer selectedCustomer = CustomerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm delete");
            alert.setHeaderText("Are you sure you want to delete " + selectedCustomer.getCustomerName() + " and their pet(s)?");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        deleteCustomer(selectedCustomer);

                        CustomerCache.flush();
                        PetCache.flush();

                        loadTableView();
//                        mainApp.showCustomerScreen();
                    });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not selected");
            alert.setHeaderText("No customer was selected to delete");
            alert.setContentText("Please select a customer to delete");
            alert.showAndWait();
        }
    }

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
                        loadTableView();
                        setDefault();;
                    });

        }
    }

    @FXML
    void handleSaveCustomer(ActionEvent event) {

        Customer selectedCustomer = CustomerTable.getSelectionModel().getSelectedItem();
        System.out.println("Debug handleSaveCustomer: editMode: " + editMode);
        if (validateCustomer()) {
            if (editMode) {
                updateCustomer(selectedCustomer);
            } else {
                saveNewCustomer();
            }
            PetCache.flush();
            CustomerCache.flush();
            loadTableView();
            setDefault();

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

    @FXML
    void handleUpdateCustomer(ActionEvent event) {

//        Customer selectedCustomer = CustomerTable.getSelectionModel().getSelectedItem();
//        mainApp.showCustomerPane(selectedCustomer);
        enableEdits();
        showButtons();
        CustomerTable.setDisable(true);
        customerLabel.setText("Update Customer Details");

        Customer selectedCustomer = CustomerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            editMode = true;
            CustomerTable.setDisable(false);
            btnCustomerAdd.setDisable(true);
            btnCustomerUpdate.setDisable(true);
            btnCustomerDelete.setDisable(true);

        } else {
            BoxBlur blur = new BoxBlur(3, 3, 3);
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            JFXButton button = new JFXButton("OK");
            button.getStyleClass().add("dialog-button");
            JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
            button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> {
                dialog.close();
            });

            dialogLayout.setHeading(new Label("No customer was selected to update."));
            dialogLayout.setActions(button);
            dialog.show();
            dialog.setOnDialogClosed((JFXDialogEvent event1) -> {
                rootAnchorPane.setEffect(null);
            });
            rootAnchorPane.setEffect(blur);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not selected");
            alert.setHeaderText("No customer was selected");
            alert.setContentText("Please select a customer to update");
            alert.showAndWait();
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param mainApp
     * @param currentUser
     */
    public void setMainController(jCalendar mainApp) {

        this.mainApp = mainApp;

        initCol();
        comboPetType.getItems().clear();
        comboPetType.setItems(Pet.getPetTypes());
        initializeStatusCombo();
        loadTableView();

        hideButtons();
        disableEdits();
        labelCusID.setText("");

        //get user ID
        String userName = "";
        Preferences userPreferences = Preferences.userRoot();
        savedUser = userPreferences.get("username", userName);

        //Populate text fields based on selected customer
        CustomerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                Customer c = CustomerTable.getSelectionModel().getSelectedItem();
                customerSelected(c);

            }
        });

//        // On selected item
//        CustomerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
//
//            @Override
//            public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
//
//                // Create a new FXML loader
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerPane2.fxml"));
//                try {
//                    // Load the another FXML file
//                    Parent newParent = loader.load();
//                    CustomerPaneController subController = loader.getController();
//                    // Set the String property
//                    // If you want to use data from the current selection: newValue contains the currently selected Person
//                    // TODO: Get value from DB
//                    subController.textToDisplay.set("value from DB for Person" + newValue.getCustomerId());
//
//                    // newParent contains the root of your other FXML file, do anything that you want to do with it (e.g. add to the current node graph)
//                    // Now I just simply open it in a new window
//                    Stage newStage = new Stage();
//                    Scene newScene = new Scene(newParent);
//                    newStage.setScene(newScene);
//                    newStage.show();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        });
        //Populate pet type and description based on selected pet
        cbPetSelection.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                Pet p = cbPetSelection.getSelectionModel().getSelectedItem();
                petSelected(p);

            }
        });

        //Show status as either A or I
        colCustomerStatus.setCellFactory(tc -> new TableCell<Customer, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null
                        : item.booleanValue() ? "A" : "I");
            }
        });

        filterStatus.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            // if the item of the list is changed
            public void changed(ObservableValue ov, Number value, Number new_value) {

                if (filterStatus.getSelectionModel().getSelectedIndex() == 0) {
                    selectedStatus.clear();
                    selectedStatus.addAll(CustomerCache.getAllCustomers());

                } else if (filterStatus.getSelectionModel().getSelectedIndex() == 1) {
                    selectedStatus.clear();
                    selectedStatus.addAll(CustomerCache.getAllActiveCustomers());

                } else if (filterStatus.getSelectionModel().getSelectedIndex() == 2) {
                    selectedStatus.clear();
                    selectedStatus.addAll(CustomerCache.getAllInactiveCustomers());
                }
            }
        });

        filterStatus.getSelectionModel().selectFirst();
        convertComboBoxString();

    }

    private void loadTableView() {

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Customer> filteredData = new FilteredList<>(selectedStatus, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (customer.getCustomerName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Customer> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(CustomerTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        CustomerTable.setItems(sortedData);

    }

    private void initCol() {

        customerLabel.setText("Customer Details");
        colCustomerID.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().customerIdProperty().get()));

        colCustomerName.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().customerNameProperty().get()));

        colCustomerPhone.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().customerPhoneProperty().get()));

        colCustomerEmail.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().customerEmailProperty().get()));
        colCustomerStatus.setCellValueFactory(cellData -> cellData.getValue().activeProperty());

    }

    private void showButtons() {
        btnCustomerCancelSave.setVisible(true);
        btnCustomerSave.setVisible(true);
        btnPetSave.setVisible(true);
        btnPetDelete.setVisible(true);
    }

    private void setDefault() {

        customerLabel.setText("Customer Details");
        CustomerTable.setDisable(false);
        labelCusID.setText("");
        cbPetSelection.setVisible(true);
        btnCustomerAdd.setDisable(false);
        btnCustomerUpdate.setDisable(false);
        btnCustomerDelete.setDisable(false);
        clearFields();
        editMode = false;
        hideButtons();

        txtCustomerName.requestFocus();
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
        labelCusID.setText("");
        txtCustomerName.clear();
        txtCustomerPhone.clear();
        txtCustomerEmail.clear();
        txtCustomerNotes.clear();
        txtPetName.clear();
        txtPetDescription.clear();
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
            Customer c = CustomerTable.getSelectionModel().getSelectedItem();
            String selectedCustomerId = c.getCustomerId();

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
                    + "Customer" + c.getCustomerName() + "\n"
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

    @FXML
    void handleUploadPhoto(ActionEvent actionEvent) {

        System.out.println("Does this pet have existing photo? " + hasPetPhoto());

        Pet p = cbPetSelection.getSelectionModel().getSelectedItem();

        boolean uploadSuccess = false;

        if (p.getPetId().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No pet selected");
            alert.setHeaderText("Please select a pet first");

            alert.showAndWait();
        } else {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("*.Images", "*.bmp", "*.png", "*.jpg", "*.gif"));

            File imgPath = fileChooser.showOpenDialog(null);
            if (imgPath != null) {
                Image image = new Image(imgPath.toURI().toString());
                petPhoto.setImage(image);

                try {

                    if (!hasPetPhoto()) {
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
                Image image = new Image(is);
                petPhoto.setImage(image);
                petLabel.setText(p.getPetName() + "'s Photo!");
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        petPhoto.setImage(null);
        petLabel.setText("No photo available");
        return false;
    }

    private void deleteCustomer(Customer customer) {

        try {
            PreparedStatement pst = DBConnection.getConn().prepareStatement("DELETE customer.*, pet.* from customer, pet WHERE customer.customerId = ? AND customer.customerId = pet.customerId");
            pst.setString(1, customer.getCustomerId());
            pst.executeUpdate();

            System.out.println("Deleted customer (and pets) for " + customer.getCustomerId() + " - " + customer.getCustomerName());

        } catch (SQLException e) {
            e.printStackTrace();
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

    private void customerSelected(Customer c) {

        labelCusID.setText(String.valueOf(c.getCustomerId()));
        txtCustomerName.setText(c.getCustomerName());
        txtCustomerPhone.setText(c.getPhone());
        txtCustomerEmail.setText(c.getEmail());
        checkboxActive.setSelected(c.getActive());
        txtCustomerNotes.setText(c.getNotes());
        //Add New Pet to dropdown list
        ObservableList<Pet> pets = PetCache.getSelectedPets(c);
        Pet p = new Pet("-1", "New Pet", "x", "x");
        pets.add(p);
//        cbPetSelection.getItems().clear();
        cbPetSelection.setItems(pets);
        cbPetSelection.getSelectionModel().selectFirst();

    }

    private void petSelected(Pet p) {
        //populate text fields for selected pet

        //hide pet name field if New Pet is selected
        if (p.getPetId().equals("-1")) {
            comboPetType.getSelectionModel().selectFirst();
            txtPetName.clear();
//            selectPetLabel.setVisible(true);
//            txtPetName.setVisible(true);
            txtPetDescription.clear();
        } else {
//            selectPetLabel.setVisible(false);
//            txtPetName.setVisible(false);
            txtPetName.setText(p.getPetName());
            comboPetType.setValue(p.getPetType());
            txtPetDescription.setText(p.getPetDesc());
            //load pet image

            //ISSUE, LOADING PET PHOTOS TO LIST, SLOW?
            hasPetPhoto();
        }

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

}
