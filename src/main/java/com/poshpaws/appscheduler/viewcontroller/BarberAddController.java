/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.poshpaws.appscheduler.dao.DBHandler;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Barber;
import com.poshpaws.appscheduler.model.User;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Jen
 */
public class BarberAddController {

    private jCalendar mainApp;
    private User currentUser;

    @FXML
    private JFXCheckBox checkboxActive;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtNotes;

    @FXML
    private Label labelBarberId;

    @FXML
    private Label topLabel;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnCancel;

    @FXML
    private ImageView btnClose;

    private String savedUser;

//    private ObservableList<Barber> barbers = FXCollections.observableArrayList();
    private boolean editMode;

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

    List<String> statusOptions = new ArrayList<>();

    String selectedId;
//    Barber selected;

    public BarberAddController() {

    }

    @FXML
    private void showBarberDetails(Barber b) {

        labelBarberId.setText(String.valueOf(b.getBarberId()));
        txtName.setText(b.nameProperty().get());
        txtPhone.setText(b.barberPhoneProperty().get());
        txtEmail.setText(b.barberEmailProperty().get());
        checkboxActive.setSelected(b.getActive());
        txtNotes.setText(b.noteProperty().get());
        datePicker.setValue(b.getHireDate());

    }

    @FXML
    void handleCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm cancel");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.showAndWait()
                // Lambda use - set OK button to respond
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    mainApp.showBarberScreen();

                });
    }

    @FXML
    void handleSave(ActionEvent event) {

        if (validateBarber()) {
            if (editMode) {
                updateBarber();
            } else {
                saveNewBarber();
            }
            mainApp.refreshView();

            mainApp.showBarberScreen();
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

        System.out.println("Running BarberAddController.setMaincontroller: Edit mode is " + editMode);
//        labelBarberId.setText("");

        Barber selectedBarber = new Barber();
        datePicker.valueProperty().bindBidirectional(selectedBarber.hireDateProperty());

        //get user ID
        String userName = "";
        Preferences userPreferences = Preferences.userRoot();
        savedUser = userPreferences.get("username", userName);

        btnClose.setOnMouseClicked((evt) -> {
            mainApp.showBarberScreen();
        });

    }

    public void setSelected(Barber selected) {

        if (selected != null) {
            selectedId = selected.getBarberId();
            topLabel.setText("Edit Barber Details");
            editMode = true;
            showBarberDetails(selected);

        } else {
            topLabel.setText("Add New Barber");
            editMode = false;
            clearFields();

        }
    }

    private void clearFields() {
        labelBarberId.setText("");
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        checkboxActive.isSelected();
        txtNotes.clear();
        datePicker.setValue(LocalDate.now());

    }

    /**
     * Inserts new barber record
     */
    private void saveNewBarber() {

        try {

//TO DO later: create active checkbox boolean
//            String status = cbStatus.getValue();
            LocalDate selHireDate = datePicker.getValue();

            java.sql.Date sqlDate = Date.valueOf(selHireDate);

            PreparedStatement pst = DBHandler.getConn().prepareStatement("INSERT INTO barber "
                    + "(barberName, barberPhone, barberEmail, notes, active, hireDate, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + " VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");

            pst.setString(1, txtName.getText());
            pst.setString(2, txtPhone.getText());
            pst.setString(3, txtEmail.getText());
            pst.setString(4, txtNotes.getText());

            if (checkboxActive.isSelected()) {
                pst.setInt(5, 1);
            } else {
                pst.setInt(5, 0);
            }

            pst.setDate(6, sqlDate);
            pst.setString(7, savedUser);
            pst.setString(8, savedUser);
            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
//        BarberTable.setItems(mainApp.getBarberData());
//        mainApp.refreshView();
    }

    /**
     * Updates customer records
     */
    private void updateBarber() {

        LocalDate selHireDate = datePicker.getValue();

        java.sql.Date sqlDate = Date.valueOf(selHireDate);
        System.out.println("sql date: " + sqlDate);
        System.out.println("attempting to update barber ID" + selectedId);
        try {

            PreparedStatement ps = DBHandler.getConn().prepareStatement("UPDATE barber "
                    + "SET barberName = ?, barberPhone = ?, barberEmail = ?, notes = ?, active = ?, hireDate = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ?  "
                    + "WHERE barberId = " + selectedId);

            ps.setString(1, txtName.getText());
            ps.setString(2, txtPhone.getText());
            ps.setString(3, txtEmail.getText());
            ps.setString(4, txtNotes.getText());

            System.out.println("Print update query " + ps);
            if (checkboxActive.isSelected()) {
                ps.setInt(5, 1);
            } else {
                ps.setInt(5, 0);
            }

            ps.setDate(6, sqlDate);
            ps.setString(7, savedUser);

            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private boolean validateBarber() {

        //EXCEPTION CONTROL: Validate nonexistent or invalid customer data
        String name = txtName.getText();
        String phone = txtPhone.getText();
        String email = txtEmail.getText();
//        String status = txtStatus.getText();
        String notes = txtNotes.getText();
        String hireDate = datePicker.getValue().toString();

        String errorMessage = "";
//
        if (name == null || name.length() == 0) {
            errorMessage += "Please enter customer name.\n";
        }
        if (email == null || email.length() == 0) {
            errorMessage += "Please enter an email address.\n";
        }
//notes optional
//        if (hireDate == null || hireDate.length() == 0) {
//            errorMessage += "Please enter a hire date.\n";
//        }

        if (phone == null || phone.length() == 0) {
            errorMessage += "Please enter a phone number).";
        } else if (phone.length() < 10 || phone.length() > 20) {
            errorMessage += "Phone number must be between 10 and 20 digits.\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid data");
            alert.setHeaderText("Please fix the following customer field(s)");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            System.out.println("Printing new barber saved for "
                    + "Name: " + name + "\n"
                    + "Phone: " + phone + "\n"
                    + "Email: " + email + "\n"
                    //                    + "Status: " + status + "\n"
                    + "Notes: " + notes + "\n"
                    + "Hire Date: " + hireDate + "\n"
            );

            return false;
        }
    }
}
