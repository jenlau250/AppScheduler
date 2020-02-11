/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.poshpaws.appscheduler.cache.AppointmentCache;
import com.poshpaws.appscheduler.cache.BarberCache;
import com.poshpaws.appscheduler.cache.CustomerCache;
import com.poshpaws.appscheduler.cache.PetCache;
import com.poshpaws.appscheduler.dao.DBHandler;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Barber;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import com.poshpaws.appscheduler.util.Loggerutil;
import com.poshpaws.appscheduler.util.Util;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Jen
 */
public class Appointment_AddController {

    private jCalendar mainApp;
    private String savedUser;
    private Appointment selectedAppt;
    private boolean editClicked;
    @FXML
    private Label topLabel;
    @FXML
    private JFXButton btnApptSave;
    @FXML
    private JFXButton btnApptCancel;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private JFXComboBox<String> comboStart;
    @FXML
    private JFXComboBox<String> comboEnd;
    @FXML
    private JFXComboBox<String> comboType;
    @FXML
    private JFXComboBox<Barber> comboBarber;
    @FXML
    private JFXTextField txtDesc;
    @FXML
    private ImageView btnClose;

    @FXML
    private JFXRadioButton choiceExistingCustomer;
    @FXML
    private ToggleGroup NewOrExistingCustomer;
    @FXML
    private JFXRadioButton choiceNewCustomer;

    @FXML
    private JFXComboBox<Customer> comboExistCustomer;

    @FXML
    private JFXComboBox<Pet> comboPet;

    @FXML
    private VBox vBoxNew;

    @FXML
    private VBox vBoxExist;

    @FXML
    private JFXTextField txtNewCustomer;

    @FXML
    private JFXTextField txtNewNumber;

    @FXML
    private JFXTextField txtNewPet;

    @FXML
    private JFXTextField txtPetDesc;

    @FXML
    private JFXComboBox<String> comboPetType;

    @FXML
    private Label apptIdLabel;

    private final DateTimeFormatter timeformat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateformat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    private final DateTimeFormatter labelformat = DateTimeFormatter.ofPattern("E MMM d, yyyy");

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

    private AppointmentCache cache;

    public Appointment_AddController() {
        this.cache = AppointmentCache.SINGLETON;
    }

    /**
     * Initializes the controller class.
     *
     * @param mainApp
     * @param currentUser
     */
    public void setMainController(jCalendar mainApp) {

        this.mainApp = mainApp;
        editClicked = false;

        //get user ID
        String userName = "";
        Preferences userPreferences = Preferences.userRoot();
        savedUser = userPreferences.get("username", userName);

        topLabel.setText("Add New Appointment");

        Appointment appt = new Appointment();
        datePicker.valueProperty().bindBidirectional(appt.startDateProperty());

        initNewFields();

        vBoxExist.setVisible(true);
        vBoxNew.setVisible(false);

        //Show existing or new customer fields
        choiceExistingCustomer.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            if (isSelected) {
                vBoxExist.setVisible(true);
                vBoxNew.setVisible(false);
            }
        });

        choiceNewCustomer.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            if (isSelected) {
                vBoxExist.setVisible(false);
                vBoxNew.setVisible(true);
                System.out.println("new customer selected");

            }
        });

        comboExistCustomer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                Customer c = comboExistCustomer.getSelectionModel().getSelectedItem();
                populatePetCombo(c);

            }
        });

        btnClose.setOnMouseClicked((evt) -> {
            mainApp.closeLeftBorder();
        });

        convertComboBoxtoString();
        comboPetType.getItems().clear();
        comboPetType.setItems(Pet.getPetTypes());
    }

    @FXML
    private void clearFields() {

        datePicker.setValue(LocalDate.now());
        comboStart.getItems().clear();
        comboEnd.getItems().clear();
        comboType.getItems().clear();
        comboBarber.getItems().clear();
        txtDesc.clear(); //Notes
        comboExistCustomer.getItems().clear();
        comboPet.getItems().clear();

    }

    @FXML
    private void showAppointmentDetails(Appointment appt) {

        apptIdLabel.setText(appt.getAppointmentId());
        datePicker.setValue(appt.getStartDate());

        //convert local time to string
        String startTime = Util.parseTimeToStringFormat(appt.getStart().toLocalTime());
        String endTime = Util.parseTimeToStringFormat(appt.getEnd().toLocalTime());
        comboStart.setValue(startTime);
        comboEnd.setValue(endTime);
        System.out.println(appt.getEnd().toLocalTime().toString());
        comboType.setValue(appt.typeProperty().get());
        txtDesc.setText(appt.descriptionProperty().get()); //Notes
        comboBarber.setValue(appt.getBarber());
        comboExistCustomer.setValue(appt.getCustomer());

    }

    private void initNewFields() {

        comboStart.getItems().clear();
        comboEnd.getItems().clear();
        comboStart.setItems(Appointment.getDefaultStartTimes());
        comboEnd.setItems(Appointment.getDefaultEndTimes());
        comboType.setItems(Appointment.getApptTypes());
        //Only show active barbers when adding new appontments
        comboBarber.setItems(BarberCache.getAllActiveBarbers());
        comboExistCustomer.setItems(CustomerCache.getAllActiveCustomers());
    }

    @FXML
    void handleApptCancel(ActionEvent event) {

        mainApp.closeLeftBorder();
    }

    @FXML
    void handleApptSave(ActionEvent event) {

        if (validateAppt()) {

            //Get Fields
            LocalDate localDate = datePicker.getValue();
            String startTime = comboStart.getSelectionModel().getSelectedItem();
            String endTime = comboEnd.getSelectionModel().getSelectedItem();

            //Convert String times to LocalTime
            LocalTime localStart = Util.parseStringToTimeFormat(startTime);
            LocalTime localEnd = Util.parseStringToTimeFormat(endTime);

            //Convert to LocalDateTime
            LocalDateTime ldtStart = LocalDateTime.of(localDate, localStart);
            LocalDateTime ldtEnd = LocalDateTime.of(localDate, localEnd);

            Barber b = comboBarber.getSelectionModel().getSelectedItem();
            Appointment check;
            if (editClicked) {
                check = selectedAppt;
                //TEST IF VALIDATE CHECK WORKS FOR UPDATING APPTS
            } else {
                check = new Appointment(ldtStart, ldtEnd, b);
            }

            if (cache.checkAppointmentOverlap(check)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Appointment Overlap");
                alert.setHeaderText("Warning: Appointment was not saved");
                alert.setContentText("Overlaps with existing appointment set for " + b.getBarberName());
                alert.showAndWait();

            } else {

                if (editClicked) {
                    updateAppointment();
                } else {
                    saveNewAppointment();
                }

                mainApp.refreshView();
                mainApp.showAppointmentListScreen();
            }

        }
    }

    public void setSelectedAppointment(Appointment appt) {
        //initialize update fields here

        selectedAppt = appt;
        editClicked = true;
        topLabel.setText("Edit Appointment Details");
        showAppointmentDetails(selectedAppt);

    }

    private Appointment getAppointmentFromFields() {

        Appointment appt = new Appointment();
        String id = apptIdLabel.getText();
        String desc = txtDesc.getText(); //notes
        String type = comboType.getValue();
        Barber b = comboBarber.getSelectionModel().getSelectedItem();

        //Get Start and End LocalDateTimes
        LocalDate localDate = datePicker.getValue();
        String startTime = comboStart.getSelectionModel().getSelectedItem();
        String endTime = comboEnd.getSelectionModel().getSelectedItem();

        //Convert String times to LocalTime
        LocalTime localStart = Util.parseStringToTimeFormat(startTime);
        LocalTime localEnd = Util.parseStringToTimeFormat(endTime);

        //Convert to LocalDateTime
        LocalDateTime ldtStart = LocalDateTime.of(localDate, localStart);
        LocalDateTime ldtEnd = LocalDateTime.of(localDate, localEnd);

        //Get new or existing customer/pet to update
        if (choiceNewCustomer.isSelected()) {
            Customer c = new Customer("1", txtNewCustomer.getText(), txtNewNumber.getText(), "NA");
            Pet p = new Pet(txtNewPet.getText(), comboPetType.getValue(), txtPetDesc.getText());
            appt = new Appointment(id, localDate, ldtStart, ldtEnd, desc, type, b, c, p);
        } else if (!choiceNewCustomer.isSelected()) {

            Customer c = comboExistCustomer.getSelectionModel().getSelectedItem();
            Pet p = comboPet.getSelectionModel().getSelectedItem();
            appt = new Appointment(id, localDate, ldtStart, ldtEnd, desc, type, b, c, p);
        }

        System.out.println("getAppointmentFromFields is " + appt);
        return appt;
    }

    private void updateAppointment() {
        System.out.println("Attempting to update appointment..");

        if (choiceNewCustomer.isSelected()) {
            if (validateNewCustomerFields()) {
                DBHandler.updateAppointmentNewCustomer(getAppointmentFromFields());

            }
        } else if (!choiceNewCustomer.isSelected()) {
            //update appt for existing pet/customer
            DBHandler.updateAppointmentExistCustomer(getAppointmentFromFields());

        }

    }

    private void saveNewAppointment() {

        if (choiceNewCustomer.isSelected()) {
            DBHandler.addAppointmentNewCustomer(getAppointmentFromFields());

        } else if (!choiceNewCustomer.isSelected()) {
            //INSERT NEW APPT FOR EXISTING CUSTOMER/PET
            DBHandler.addAppointmentExistCustomer(getAppointmentFromFields());
        }
    }

    private boolean validateAppt() {

        //Validate required fields
        // optional String desc = txtDesc.getText();
        Barber barber = comboBarber.getValue();
        Pet pet = comboPet.getValue();
        Customer customer = comboExistCustomer.getValue();
        String start = comboStart.getValue();
        String end = comboEnd.getValue();
        LocalDate date = datePicker.getValue();
        String type = comboType.getValue();

        String errorMessage = "";

        //edit: add date logic
        if (start == null || start.length() == 0) {
            errorMessage += "Please enter a start time.\n";
        }

        if (end == null || end.length() == 0) {
            errorMessage += "Please enter an end time.\n";
        }

        //edit: check for localdate
        if (date == null) {
            errorMessage += "Please choose appointment date.\n";
        }
        if (type == null || type.length() == 0) {
            errorMessage += "Please select appointment type.\n";
        }

        //edit: check for objects
        if (barber == null) {
            errorMessage += "Please select a barber.\n";
        }

        if (choiceExistingCustomer.isSelected()) {
            //edit: check for objects
            if (pet == null) {
                errorMessage += "Please select a pet.\n";
            }

            //edit: check for objects
            if (customer == null) {
                errorMessage += "Please select a customer.\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid data");
            alert.setHeaderText("Please fix the following customer field(s)");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

    private boolean validateNewCustomerFields() {

        //If new customer radio button is selected, check whether forms are filled out or not
        //if yes, insert new customer and pet records
        //if not, add new appointment with no customer and pet data
        //Validate required fields
        // optional String desc = txtDesc.getText();
        String name = txtNewCustomer.getText();
        String phone = txtNewNumber.getText();
        String petName = txtNewPet.getText();
        String petType = comboPetType.getValue();

        String errorMessage = "";

        //edit: add date logic
        if (name == null || name.length() == 0) {
            errorMessage += "Missing customer name.\n";
        }

        if (phone == null || phone.length() == 0) {
            errorMessage += "Missing phone number.";
        } else if (phone.length() < 10 || phone.length() > 20) {
            errorMessage += "Phone number must be between 10 and 20 digits.\n";
        }

        if (petName == null || petName.length() == 0) {
            errorMessage += "Missing pet name\n";
        }

        if (petType == null || petType.length() == 0) {
            errorMessage += "Missing pet type\n";
        }

        if (errorMessage.length() == 0) {
            return true;

            //then create new records
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("New customer and pet info not entered");
            alert.setHeaderText("Create new appointment without customer and pet info anyway");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

    public Boolean checkAppointmentOverlap(Appointment a) {
        boolean overlap = false;

        List<Appointment> appointmentsForBarber = AppointmentCache.getBarberAppointments(a.getBarber().getBarberId());

        //add this appointment after checking for conflicts
//        appointmentsForBarber.remove(appointment);
        for (Appointment other : appointmentsForBarber) {
            if (a.getStart().isBefore(other.getEnd()) && (a.getEnd().isAfter(other.getStart()))) {
                overlap = true;
            }
        }
        return overlap;
    }

    public Boolean validateApptOverlap(LocalDateTime ldtStart, LocalDateTime ldtEnd, Barber b) {
        System.out.println("Validating appointment times.. ");
        Boolean overlap = false;

        System.out.println("Timestamp WHERE " + ldtStart + " between start and end or " + ldtEnd
                + "\n between start and end or " + ldtStart + " < start and " + ldtEnd + " with barberId " + b.getBarberId()
        );

        //Convert datetime for database ex: 2020-02-11T22:00Z[UTC]
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime startUTC = ldtStart.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = ldtEnd.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));

        Timestamp startsql = Timestamp.valueOf(startUTC.toLocalDateTime());
        Timestamp endsql = Timestamp.valueOf(endUTC.toLocalDateTime());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stringStart = dateFormatter.format(startsql);
        String stringEnd = dateFormatter.format(endsql);

//        System.out.println("Timestamp " + sDate.toLocalDateTime());
//Appointment start and end time can't overlap for same barber
        try {
            PreparedStatement ps = DBHandler.getConn().prepareStatement(
                    "SELECT * FROM appointment "
                    + "WHERE (? BETWEEN start AND end AND barberId = ? "
                    + "OR ? BETWEEN start AND end AND barberId = ? "
                    + "OR ? < start AND ? > end AND barberId = ?) "
            );
            ps.setString(1, stringStart);
            ps.setString(2, b.getBarberId());
            ps.setString(3, stringEnd);
            ps.setString(4, b.getBarberId());
            ps.setString(5, stringStart);
            ps.setString(6, stringEnd);
            ps.setString(7, b.getBarberId());
            ResultSet rs = ps.executeQuery();

            if (rs.absolute(1)) {

                overlap = true;
            }

        } catch (SQLException ex) {
            System.out.println("Check SQL Exception " + ex);
        }

        System.out.println("Results of overlap? " + overlap);
        return overlap;

    }

    @FXML
    public void initialize() {

    }

    private void populatePetCombo(Customer c) {
        comboPet.setItems(PetCache.getSelectedPets(c));
        comboPet.getSelectionModel().selectFirst();

    }

    private void convertComboBoxtoString() {
        //Add String converter to convert barber and customer objects
        comboBarber.setConverter(new StringConverter<Barber>() {
            @Override
            public String toString(Barber object) {
                if (object == null) {
                    return null;
                } else {
                    return object.nameProperty().get();
                }
            }

            @Override
            public Barber fromString(String string) {
                return comboBarber.getItems().stream().filter(ap -> ap.nameProperty().get().equals(string)).findFirst().orElse(null);
            }
        });

        comboExistCustomer.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer object) {
                if (object == null) {
                    return null;
                } else {
                    return object.customerNameProperty().get();
                }
            }

            @Override
            public Customer fromString(String string) {
                return comboExistCustomer.getItems().stream().filter(ap -> ap.customerNameProperty().get().equals(string)).findFirst().orElse(null);
            }
        });

        comboPet.setConverter(new StringConverter<Pet>() {
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
                return comboPet.getItems().stream().filter(ap -> ap.nameProperty().get().equals(string)).findFirst().orElse(null);
            }
        });
    }

}
