/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.poshpaws.appscheduler.cache.AppointmentCache;
import com.poshpaws.appscheduler.dao.DBHandler;
import com.poshpaws.appscheduler.AppScheduler;
import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Jen
 */
public class AppointmentListController implements Initializable {

    private AppScheduler mainApp;
    private LocalDate currDate;

    @FXML
    private TableView<Appointment> tableView;

    @FXML
    private TableColumn<Appointment, String> colType;

    @FXML
    private TableColumn<Appointment, String> colDesc;

    @FXML
    private TableColumn<Appointment, String> colCusName;

    @FXML
    private TableColumn<Appointment, LocalDate> colDate;

    @FXML
    private TableColumn<Appointment, LocalDateTime> colStart;

    @FXML
    private TableColumn<Appointment, LocalDateTime> colEnd;

    @FXML
    private TableColumn<Appointment, String> colBarber;

    @FXML
    private TableColumn<Appointment, String> colPet;

    @FXML
    private JFXButton btnViewCustomer;

    @FXML
    private Label labelStartBound;
    @FXML
    private Label labelEndBound;

    @FXML
    private JFXComboBox comboWeekMonth;
    @FXML
    private JFXButton btnBack;
    @FXML
    private JFXButton btnNext;

    private final DateTimeFormatter timeformat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateformat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private static final DateTimeFormatter DATEFORMATTOLOCAL = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private final DateTimeFormatter labelformat = DateTimeFormatter.ofPattern("E MMM d, yyyy");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    @FXML
    void handleApptAddNew(ActionEvent event) {

        mainApp.showAppointmentAddScreen();
    }

    @FXML
    void handleApptEdit(ActionEvent event) {

        Appointment selectedAppt = tableView.getSelectionModel().getSelectedItem();

        if (selectedAppt != null) {
            mainApp.showAppointmentAddScreen(selectedAppt);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not selected");
            alert.setContentText("Please select an existing appointment to update");
            alert.showAndWait();
        }
    }

    @FXML
    void handleCustomerView(ActionEvent event) {

        Appointment selectedAppt = tableView.getSelectionModel().getSelectedItem();

        if (selectedAppt != null) {

            mainApp.showCustomerPane(selectedAppt.getCustomer());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not selected");
            alert.setContentText("Please select an existing appointment to view customer details");
            alert.showAndWait();
        }

    }

    @FXML
    void handleApptDelete(ActionEvent event) {
        Appointment selectedAppt = tableView.getSelectionModel().getSelectedItem();

        if (selectedAppt == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nothing selected");
            alert.setHeaderText("No appointment was selected to delete");
            alert.setContentText("Please select an appointment to delete");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete " + " scheduled for " + selectedAppt.getCustomer().getCustomerName() + "?");
            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        deleteAppointment(selectedAppt);
                        mainApp.showAppointmentListScreen();
                    });
        }

    }

    /**
     * Initializes the controller class.This method is automatically called
     * after the fxml file has been loaded. Add observable list data to table
     * view here
     *
     * @param mainApp
     */
    public void setMainController(AppScheduler mainApp) {

        this.mainApp = mainApp;
        appointmentList.addAll(AppointmentCache.getAllAppointments());
        tableView.setItems(appointmentList);

    }

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public AppointmentListController() {

    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {

        //1 - INITIALIZE THE TABLECOLUMNS
        //this method replaces newPropertyValueFactory<>"date"
        colDate.setCellValueFactory(f -> f.getValue().startDateProperty());
        colStart.setCellValueFactory(f -> f.getValue().startProperty());
        colEnd.setCellValueFactory(f -> f.getValue().endProperty());
        colDesc.setCellValueFactory(f -> f.getValue().descriptionProperty());
        colType.setCellValueFactory(f -> f.getValue().typeProperty());

        colBarber.setCellValueFactory(f -> f.getValue().getBarber().nameProperty());
        colPet.setCellValueFactory(f -> f.getValue().getPet().nameProperty());
        colCusName.setCellValueFactory(f -> f.getValue().getCustomer().customerNameProperty());

        colStart.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime datetime, boolean empty) {
                super.updateItem(datetime, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(datetime.atZone(ZoneOffset.UTC)
                            .format(timeFormatter));
                }
            }
        });

        colEnd.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime datetime, boolean empty) {
                super.updateItem(datetime, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(datetime.atZone(ZoneOffset.UTC)
                            .format(timeFormatter));
                }
            }
        });

        //default show month label
        labelStartBound.setText(null);
        labelEndBound.setText(null);

        currDate = LocalDate.now();
        // Add monthly or weekly filter to tableview
        comboWeekMonth.setItems(FXCollections.observableArrayList("Daily", "Weekly", "Monthly"));

        comboWeekMonth.setValue("Monthly");
        comboWeekMonth.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            // if the item of the list is changed
            public void changed(ObservableValue ov, Number value, Number new_value) {
                LocalDate cbStartDate = currDate;
                if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 0) {
                    nextDay(cbStartDate);
                } else if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 1) {
                    nextWeek(cbStartDate);
                } else if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 2) {
                    nextMonth(cbStartDate);
                }

            }
        });

        btnNext.setOnAction((evt) -> {
            LocalDate cbStartDate = currDate;
            if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 0) {
                nextDay(cbStartDate);
            } else if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 1) {
                nextWeek(cbStartDate);
            } else if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 2) {
                nextMonth(cbStartDate);
            }
        });

        btnBack.setOnAction((evt) -> {
            LocalDate cbEndDate = currDate;
            if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 0) {
                previousDay(cbEndDate);
            } else if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 1) {
                previousWeek(cbEndDate);
            } else if (comboWeekMonth.getSelectionModel().getSelectedIndex() == 2) {
                previousMonth(cbEndDate);
            }
        });

    }

    private void nextMonth(LocalDate cbStartDate) {

        currDate = currDate.plusMonths(1);
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentList);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = row.getStartDate();
            return rowDate.isAfter(cbStartDate.minusDays(1)) && rowDate.isBefore(currDate);
        });
        tableView.setItems(filteredData);

        labelStartBound.setText(cbStartDate.format(labelformat));
        labelEndBound.setText(currDate.format(labelformat));
    }

    private void previousMonth(LocalDate cbEndDate) {
        currDate = currDate.minusMonths(1);
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentList);
        filteredData.setPredicate(row -> {
            LocalDate rowDate = row.getStartDate();
            return rowDate.isAfter(currDate.minusDays(1)) && rowDate.isBefore(cbEndDate);
        });
        tableView.setItems(filteredData);

        labelStartBound.setText(currDate.format(labelformat));
        labelEndBound.setText(cbEndDate.format(labelformat));
    }

    private void nextWeek(LocalDate cbStartDate) {
        currDate = currDate.plusWeeks(1);
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentList);
        filteredData.setPredicate(row -> {
            LocalDate rowDate = row.getStartDate();
            return rowDate.isAfter(cbStartDate.minusDays(1)) && rowDate.isBefore(currDate);
        });
        tableView.setItems(filteredData);
        labelStartBound.setText(cbStartDate.format(labelformat));
        labelEndBound.setText(currDate.format(labelformat));
    }

    private void previousWeek(LocalDate cbEndDate) {
        currDate = currDate.minusWeeks(1);
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentList);
        filteredData.setPredicate(row -> {
            LocalDate rowDate = row.getStartDate();
            return rowDate.isAfter(currDate.minusDays(1)) && rowDate.isBefore(cbEndDate);
        });
        tableView.setItems(filteredData);
        labelStartBound.setText(currDate.format(labelformat));
        labelEndBound.setText(cbEndDate.format(labelformat));
    }

    private void nextDay(LocalDate cbStartDate) {
        currDate = currDate.plusDays(1);
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentList);
        filteredData.setPredicate(row -> {
            LocalDate rowDate = row.getStartDate();
            return rowDate.isAfter(cbStartDate.minusDays(1)) && rowDate.isBefore(currDate);
        });
        tableView.setItems(filteredData);
        labelStartBound.setText(cbStartDate.format(labelformat));
        labelEndBound.setText(currDate.format(labelformat));
    }

    private void previousDay(LocalDate cbEndDate) {
        currDate = currDate.minusDays(1);
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentList);
        filteredData.setPredicate(row -> {
            LocalDate rowDate = row.getStartDate();
            return rowDate.isAfter(currDate.minusDays(1)) && rowDate.isBefore(cbEndDate);
        });
        tableView.setItems(filteredData);
        labelStartBound.setText(currDate.format(labelformat));
        labelEndBound.setText(cbEndDate.format(labelformat));
    }

    private void deleteAppointment(Appointment a) {

        if (DBHandler.deleteAppointment(a)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment deleted");
            alert.setHeaderText("Appointment was successfully deleted.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Appointmet deleting failed");
            alert.setHeaderText("Please try again.");
            alert.showAndWait();
        }

        AppointmentCache.flush();

    }

}
