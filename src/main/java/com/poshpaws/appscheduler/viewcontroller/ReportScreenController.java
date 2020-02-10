/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.poshpaws.appscheduler.cache.AppointmentCache;
import com.poshpaws.appscheduler.cache.BarberCache;
import com.poshpaws.appscheduler.dao.DBHandler;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Barber;
import com.poshpaws.appscheduler.model.Pet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author jlau2
 */
public class ReportScreenController {

    private jCalendar mainApp;

    @FXML
    private TableView<Appointment> tblSchedule;
    @FXML
    private TableColumn<Appointment, LocalDateTime> colEnd;
    @FXML
    private TableColumn<Appointment, String> colType;
    @FXML
    private TableColumn<Appointment, String> colPetType;

    @FXML
    private TableColumn<Appointment, LocalDate> colStartDate;
    @FXML
    private TableColumn<Appointment, LocalDateTime> colStart;
    @FXML
    private TableColumn<Appointment, String> colBarber;

    @FXML
    private TableView<Appointment> tblApptType;
    @FXML
    private TableColumn<Appointment, String> colApptType;
    @FXML
    private TableColumn<Appointment, String> colApptCount;
    @FXML
    private TableColumn<Appointment, Barber> colApptBarber;

    @FXML
    private JFXDatePicker datePickerSchlTo;

    @FXML
    private JFXDatePicker datePickerSchlFrom;

    @FXML
    private JFXComboBox comboMonth;

    private final DateTimeFormatter dtformat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private final ZoneId newZoneId = ZoneId.systemDefault();
    private ObservableList<Appointment> apptList;
    private ObservableList<Pet> petList;
    private ObservableList<Appointment> schedule;
    private ObservableSet<String> monthSet;
    FilteredList<Appointment> filteredData;

    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

    public ReportScreenController() {

    }

    /**
     * Initializes the controller class.
     *
     * @param mainApp
     */
    public void setMainController(jCalendar mainApp) {

        this.mainApp = mainApp;

        populateApptTypeList();
        populateSchedule();

        initCol();

        setDefaultDates();

        ObservableList<String> newList = FXCollections.observableArrayList(monthSet);

        comboMonth.setItems(newList);
        // Update month filter data
        comboMonth.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov,
                    final String oldvalue, final String newvalue) {
                filterMonth(newvalue);

            }

        });

        //format start and end times
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

        filterScheduleDates();

    }

    private void filterMonth(String month) {
        FilteredList<Appointment> filteredData = new FilteredList<>(apptList);
        filteredData.setPredicate(row -> {
            String rowMonth = row.getMonth();
            return rowMonth.equals(month);
        });

        tblApptType.setItems(filteredData);
    }

    private void filterScheduleDates() {

        //check to make sure second date is older than the first
        FilteredList<Appointment> filteredItems = new FilteredList<>(schedule);

        filteredItems.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            LocalDate minDate = datePickerSchlFrom.getValue();
            LocalDate maxDate = datePickerSchlTo.getValue();

            //get final values != null
            final LocalDate finalMin = minDate == null ? LocalDate.MIN : minDate;
            final LocalDate finalMax = maxDate == null ? LocalDate.MAX : maxDate;

            // values for openDate need to be in the interval
            return ti -> !finalMin.isAfter(ti.getStartDate()) && !finalMax.isBefore(ti.getStartDate());
        },
                datePickerSchlFrom.valueProperty(),
                datePickerSchlTo.valueProperty()));

        tblSchedule.setItems(filteredItems);
    }

    private void populateApptTypeList() {

        apptList = FXCollections.observableArrayList();
        monthSet = FXCollections.observableSet();
        try {

            PreparedStatement statement = DBHandler.getConn().prepareStatement(
                    "SELECT MONTHNAME(`start`) AS \"month\", barberId, type AS \"type\", COUNT(*) as \"count\" "
                    + "FROM appointment "
                    + "GROUP BY MONTHNAME(`start`), barberId, type "
                    + "ORDER BY count desc");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {

                Barber barber = BarberCache.getBarber(rs.getString("barberId"));
                String month = rs.getString("month");
                String type = rs.getString("type");
                String count = rs.getString("count");
                apptList.add(new Appointment(month, barber, type, count));
                monthSet.add(month);
            }

        } catch (SQLException sqe) {
            sqe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tblApptType.setItems(apptList);
    }

    private void populateSchedule() {

        schedule = FXCollections.observableArrayList();
        schedule.addAll(AppointmentCache.getAllAppointments());
        tblSchedule.getItems().setAll(schedule);
    }

    private void initCol() {
        //appointment report
        colBarber.setCellValueFactory(f -> f.getValue().getBarber().nameProperty());
        colStartDate.setCellValueFactory(f -> f.getValue().startDateProperty());
        colStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPetType.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPet().getPetType()));

        //barber report
        colApptType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colApptBarber.setCellValueFactory(new PropertyValueFactory<>("barber"));
        colApptCount.setCellValueFactory(new PropertyValueFactory<>("count"));

    }

    private void setDefaultDates() {
        LocalDate currDate = LocalDate.now();
        LocalDate startDate = currDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = currDate.with(TemporalAdjusters.lastDayOfMonth());

        datePickerSchlFrom.setValue(startDate);
        datePickerSchlTo.setValue(endDate);

    }
}
