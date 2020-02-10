/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.poshpaws.appscheduler.cache.BarberCache;
import com.poshpaws.appscheduler.dao.DBHandler;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Barber;
import com.poshpaws.appscheduler.model.User;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
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
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author jlau2
 */
public class BarberScreenController {

    private jCalendar mainApp;
    private User currentUser;
    @FXML
    private TableView<Barber> BarberTable;

    @FXML
    private TableColumn<Barber, String> colID;

    @FXML
    private TableColumn<Barber, String> colName;

    @FXML
    private TableColumn<Barber, String> colPhone;

    @FXML
    private TableColumn<Barber, String> colEmail;
    @FXML
    private TableColumn<Barber, Boolean> colStatus;
    @FXML
    private TableColumn<Barber, String> colNotes;
    @FXML
    private TableColumn<Barber, String> colHireDate;

    @FXML
    private JFXTextField filterBarber;
    @FXML
    private JFXComboBox filterStatus;

    @FXML
    private Label barberLabel;

    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXButton btnUpdate;
    @FXML
    private JFXButton btnDelete;

//    private ObservableList<Barber> barbers = FXCollections.observableArrayList();
    private ObservableList<Barber> selectedStatus = FXCollections.observableArrayList();
    private boolean editMode;

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

    List<String> statusOptions = new ArrayList<>();

    public BarberScreenController() {

    }

    @FXML
    void handleAdd(ActionEvent event) {

        btnAdd.setDisable(true);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        mainApp.showBarberAddScreen();

    }

    @FXML
    void handleDelete(ActionEvent event) {

        Barber selectedBarber = BarberTable.getSelectionModel().getSelectedItem();

        if (selectedBarber != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm delete");
            alert.setHeaderText("Are you sure you want to delete " + selectedBarber.getBarberName() + "?");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        deleteBarber(selectedBarber);
                        BarberCache.flush();
                    });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not selected");
            alert.setHeaderText("No barber was selected to delete");
            alert.setContentText("Please select a barber to delete");
            alert.showAndWait();
        }
        BarberCache.flush();
        loadTableView();
    }

    @FXML
    void handleUpdate(ActionEvent event) {

        Barber selectedBarber = BarberTable.getSelectionModel().getSelectedItem();
        if (selectedBarber != null) {
            btnAdd.setDisable(true);
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
            mainApp.showBarberAddScreen(selectedBarber);

        } else {
            if (selectedBarber != null) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not selected");
//            alert.setHeaderText("No barber was selected to delete");
                alert.setContentText("Please select a barber to update");
                alert.showAndWait();
            }
        }

    }

    @FXML
    private void initializeStatusCombo() {

        filterStatus.getItems().addAll("All", "Active", "Inactive");

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
        initializeStatusCombo();
        loadTableView();

        filterStatus.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            // if the item of the list is changed
            public void changed(ObservableValue ov, Number value, Number new_value) {

                if (filterStatus.getSelectionModel().getSelectedIndex() == 0) {
                    selectedStatus.clear();
                    selectedStatus.addAll(BarberCache.getAllBarbers());

                } else if (filterStatus.getSelectionModel().getSelectedIndex() == 1) {
                    selectedStatus.clear();
                    selectedStatus.addAll(BarberCache.getAllActiveBarbers());

                } else if (filterStatus.getSelectionModel().getSelectedIndex() == 2) {
                    selectedStatus.clear();
                    selectedStatus.addAll(BarberCache.getAllInactiveBarbers());
                }
            }
        });

        filterStatus.getSelectionModel().selectFirst();

        //Show status as either A or I
        colStatus.setCellFactory(tc -> new TableCell<Barber, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null
                        : item.booleanValue() ? "A" : "I");
            }
        });

    }

    private void loadTableView() {

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Barber> filteredData = new FilteredList<>(selectedStatus, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterBarber.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(b -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (b.getBarberName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
//                } else if (customer.getLastName().toLowerCase().contains(lowerCaseFilter)) {
//                    return true; // Filter matches last name.
//                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Barber> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(BarberTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        BarberTable.setItems(sortedData);

    }

    private void initCol() {

        //Load Barber Tableview
        barberLabel.setText("Barber Details");
        colID.setCellValueFactory(new PropertyValueFactory<>("barberId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("barberName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("barberPhone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("barberEmail"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));

    }

    private void deleteBarber(Barber selectedBarber) {

        try {
            PreparedStatement pst = DBHandler.getConn().prepareStatement(
                    "DELETE FROM barber WHERE barberId = ?");
            pst.setString(1, selectedBarber.getBarberId());
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        BarberCache.flush();
        loadTableView();

    }

}
