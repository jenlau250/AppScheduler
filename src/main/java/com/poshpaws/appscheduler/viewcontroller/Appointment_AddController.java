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
import com.poshpaws.appscheduler.cache.BarberCache;
import com.poshpaws.appscheduler.cache.CustomerCache;
import com.poshpaws.appscheduler.cache.PetCache;
import com.poshpaws.appscheduler.dao.DBConnection;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Barber;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import com.poshpaws.appscheduler.util.DateTimeUtil;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    // Controllers
//    private AppointmentScreenController mainController;
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

    private ObservableList<Appointment> data;
    private ObservableList<Pet> selectedPets = FXCollections.observableArrayList();
    private ObservableList<Customer> customers = FXCollections.observableArrayList();

//    Customer customer;
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
        String startTime = DateTimeUtil.parseTimeToStringFormat(appt.getStart().toLocalTime());
        String endTime = DateTimeUtil.parseTimeToStringFormat(appt.getEnd().toLocalTime());
        comboStart.setValue(startTime);
        comboEnd.setValue(endTime);
        System.out.println(appt.getEnd().toLocalTime().toString());
        comboType.setValue(appt.typeProperty().get());
        txtDesc.setText(appt.descriptionProperty().get()); //Notes
        comboBarber.setValue(appt.getBarber());
        comboExistCustomer.setValue(appt.getCustomer());

        //Update Pet Observable List based on selected appt
//        comboExistPet.setValue(appt.getPet());
    }

    private void initNewFields() {

        comboStart.setItems(Appointment.getDefaultStartTimes());
        comboEnd.setItems(Appointment.getDefaultEndTimes());
        //Get Appointment Type options
        comboType.setItems(Appointment.getApptTypes());
//Only show ACTIVE barbers when adding new appontments
        comboBarber.setItems(BarberCache.getAllActiveBarbers());
        comboExistCustomer.setItems(CustomerCache.getAllActiveCustomers());
    }

    @FXML
    void handleApptCancel(ActionEvent event) {

        mainApp.closeLeftBorder();
    }

    @FXML
    void handleApptSave(ActionEvent event) {

//        System.out.println("printing local time " + startTime + " to " + endTime + "\n");
//        System.out.println("printing zoned time " + startUTC + " to " + endUTC + "\n");
//        System.out.println("printing sql time " + startsql + " to " + endsql + "\n");
//        System.out.println("printing string sql time " + stringStart + " to " + stringEnd + "\n");
        if (validateAppt()) {

            //Get Fields
            LocalDate localDate = datePicker.getValue();
            String startTime = comboStart.getSelectionModel().getSelectedItem();
            String endTime = comboEnd.getSelectionModel().getSelectedItem();

            //Convert String times to LocalTime
            LocalTime localStart = DateTimeUtil.parseStringToTimeFormat(startTime);
            LocalTime localEnd = DateTimeUtil.parseStringToTimeFormat(endTime);

            //Convert to LocalDateTime
            LocalDateTime startDate = LocalDateTime.of(localDate, localStart);
            LocalDateTime endDate = LocalDateTime.of(localDate, localEnd);

            //Convert datetime for database ex: 2020-02-11T22:00Z[UTC]
            ZoneId zid = ZoneId.systemDefault();
            ZonedDateTime startUTC = startDate.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endUTC = endDate.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));

            Timestamp startsql = Timestamp.valueOf(startUTC.toLocalDateTime());
            Timestamp endsql = Timestamp.valueOf(endUTC.toLocalDateTime());

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String stringStart = dateFormatter.format(startsql);
            String stringEnd = dateFormatter.format(endsql);

            Barber b = comboBarber.getSelectionModel().getSelectedItem();

            if (validateApptOverlap(stringStart, stringEnd, b)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Appointment Overlap");
                alert.setHeaderText("Warning: Appointment was not saved");
                alert.setContentText("Overlaps with existing appointment set for " + b.getBarberName());
                alert.showAndWait();

            } else {

                if (editClicked) {
                    updateAppointment(startsql, endsql);
                } else {
                    saveNewAppointment(startsql, endsql);
                }

                //Return to list
                mainApp.showAppointmentListScreen();
            }

        }
    }

    public void setSelectedAppointment(Appointment appt) {
        //initialize update fields here
        editClicked = true;
        topLabel.setText("Edit Appointment Details");
        showAppointmentDetails(appt);

    }

    private void updateAppointment(Timestamp startsql, Timestamp endsql) {
        System.out.println("Attempting to update appointment..");

        //common fields to get
        String sDesc = txtDesc.getText(); //notes
        String sType = comboType.getValue();
        String sBarber = comboBarber.getValue().getBarberId();
        String existCustomerId = comboExistCustomer.getValue().getCustomerId();
        String existPetId = comboPet.getValue().getPetId();
        int newCustomerId = -1;
        int newPetId = -1;

        System.out.println("New toggle is selected " + choiceNewCustomer.isSelected());

        if (choiceNewCustomer.isSelected()) {
            if (validateNewCustomerFields()) {
                try {

                    //INSERT NEW CUSTOMER RECORD
                    PreparedStatement ps = DBConnection.getConn().prepareStatement("INSERT INTO customer "
                            + "( customerName, customerPhone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                            + " VALUES ( ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, txtNewCustomer.getText());
                    ps.setString(2, txtNewNumber.getText());
                    ps.setString(3, savedUser);
                    ps.setString(4, savedUser);

                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();

                    if (rs.next()) {
                        newCustomerId = rs.getInt(1);
                    }

                    //INSERT NEW PET RECORD
                    PreparedStatement ps2 = DBConnection.getConn().prepareStatement("INSERT INTO pet "
                            + "(petName, petType, petDescription, createDate, createdBy, lastUpdate, lastUpdateBy, customerId) "
                            + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    ps2.setString(1, txtNewPet.getText());
                    ps2.setString(2, comboPetType.getValue());
                    ps2.setString(3, txtPetDesc.getText());
                    ps2.setString(4, savedUser);
                    ps2.setString(5, savedUser);
                    ps2.setInt(6, newCustomerId);

                    ps2.executeUpdate();
                    ResultSet rs2 = ps2.getGeneratedKeys();

                    if (rs2.next()) {
                        newPetId = rs2.getInt(1);
                    }

                    //UPDATE NEW APPT WITH NEW CUSTOMER/PET
                    PreparedStatement pst = DBConnection.getConn().prepareStatement("UPDATE appointment "
                            + "SET customerId = ?, barberId = ?, petId = ?, start= ?, end = ?, description = ?, type = ?, lastUpdate= CURRENT_TIMESTAMP, lastUpdateBy = ? "
                            + "WHERE appointmentId = ? ");

                    pst.setInt(1, newCustomerId);
                    pst.setString(2, sBarber);
                    pst.setInt(3, newPetId);

                    pst.setTimestamp(4, startsql);
                    pst.setTimestamp(5, endsql);

                    pst.setString(6, sDesc);
                    pst.setString(7, sType);
                    pst.setString(8, savedUser);
                    pst.setString(9, apptIdLabel.getText());
                    int result = pst.executeUpdate();
                    if (result == 1) {//one row was affected; namely the one that was inserted!
                        System.out.println("YAY! Updated Appt");

                    } else {
                        System.out.println("BOO! didn't update appt");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                System.out.println("Updating appointment record to save to new customer: "
                        + " Appointment ID " + apptIdLabel.getText() + " "
                        + " Desc " + sDesc + " "
                        + " Type " + sType + " "
                        + " Barber " + sBarber + " " + comboBarber.getValue().nameProperty().get()
                        + " New Customer " + newCustomerId + " - " + txtNewCustomer.getText()
                        + " New Pet " + newPetId + " - " + txtNewPet.getText());
            }
        } else if (!choiceNewCustomer.isSelected()) {
            //update appt for existing pet/customer
            try {

                PreparedStatement ps4 = DBConnection.getConn().prepareStatement("UPDATE appointment "
                        + "SET customerId = ?, barberId = ?, petId = ?, start= ?, end = ?, description = ?, type = ?, lastUpdate= CURRENT_TIMESTAMP, lastUpdateBy = ? "
                        + "WHERE appointmentId = ?");

                ps4.setString(1, comboExistCustomer.getValue().getCustomerId());
                ps4.setString(2, sBarber);
                ps4.setString(3, comboPet.getValue().getPetId());

                ps4.setTimestamp(4, startsql);
                ps4.setTimestamp(5, endsql);

                ps4.setString(6, sDesc);
                ps4.setString(7, sType);
                ps4.setString(8, savedUser);
                ps4.setString(9, apptIdLabel.getText());
//            pst.setString(9, currentUser.getUserName());
                int result = ps4.executeUpdate();
                if (result == 1) {//one row was affected; namely the one that was inserted!
                    System.out.println("YAY! Updated Appt");

                } else {
                    System.out.println("BOO! didn't update appt");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            System.out.println("Updating appointment record to save to existing customer: "
                    + " Appointment ID " + apptIdLabel.getText() + " "
                    + " Desc " + sDesc + " "
                    + " Type " + sType + " "
                    + " Barber " + sBarber + " " + comboBarber.getValue().nameProperty().get()
                    + " Existing Customer " + existCustomerId + " - " + comboExistCustomer.getValue().customerNameProperty().get()
                    + " Existing Pet " + existPetId + " - " + comboPet.getValue().nameProperty().get()
            );

        }
        mainApp.refreshView();

        //        String sContact = currentUser.getUserName(); //CHANGE LATER
    }

    private void saveNewAppointment(Timestamp startsql, Timestamp endsql) {

        String sType = comboType.getValue();
        String sBarber = comboBarber.getValue().getBarberId();
        String sDesc = txtDesc.getText();
        String existCustomerId = comboExistCustomer.getValue().getCustomerId();
        String existPetId = comboPet.getValue().getPetId();
        int newCustomerId = -1;
        int newPetId = -1;

//        String sContact = currentUser.getUserName(); //CHANGE LATER
        if (choiceNewCustomer.isSelected()) {

            try {

                //INSERT NEW CUSTOMER RECORD
                PreparedStatement ps = DBConnection.getConn().prepareStatement("INSERT INTO customer "
                        + "( customerName, customerPhone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + " VALUES ( ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, txtNewCustomer.getText());
                ps.setString(2, txtNewNumber.getText());
                ps.setString(3, savedUser);
                ps.setString(4, savedUser);

                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    newCustomerId = rs.getInt(1);
                }

                //INSERT NEW PET RECORD
                PreparedStatement ps2 = DBConnection.getConn().prepareStatement("INSERT INTO pet "
                        + "(petName, petType, petDescription, createDate, createdBy, lastUpdate, lastUpdateBy, customerId) "
                        + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps2.setString(1, txtNewPet.getText());
                ps2.setString(2, comboPetType.getValue());
                ps2.setString(3, txtPetDesc.getText());
                ps2.setString(4, savedUser);
                ps2.setString(5, savedUser);
                ps2.setInt(6, newCustomerId);

                ps2.executeUpdate();
                ResultSet rs2 = ps2.getGeneratedKeys();

                if (rs2.next()) {
                    newPetId = rs2.getInt(1);
                }

                //INSERT NEW APPT WITH NEW CUSTOMER/PET
                PreparedStatement pst = DBConnection.getConn().prepareStatement("INSERT INTO appointment "
                        + "(customerId, barberId, petId, start, end, description, type, createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");

                pst.setInt(1, newCustomerId);
                pst.setString(2, sBarber);
                pst.setInt(3, newPetId);

                pst.setTimestamp(4, startsql);
                pst.setTimestamp(5, endsql);

                pst.setString(6, sDesc);
                pst.setString(7, sType);
                pst.setString(8, savedUser);
                pst.setString(9, savedUser);

                int result = pst.executeUpdate();
                if (result == 1) {//one row was affected; namely the one that was inserted!
                    System.out.println("YAY! New Appointment Save");

                } else {
                    System.out.println("BOO! New Appointment Save");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            System.out.println("Printing  new appointment for existing customer: "
                    //                + " Title " + sTitle + " "
                    + " Desc " + sDesc + " "
                    + " Type " + sType + " "
                    + " Barber " + sBarber + " " + comboBarber.getValue().nameProperty().get()
                    + " Customer " + existCustomerId + " " + comboExistCustomer.getValue().customerNameProperty().get()
                    + " Pet " + existPetId + " " + comboPet.getValue().nameProperty().get()
            );
        } else if (!choiceNewCustomer.isSelected()) {
            //INSERT NEW APPT WITH NEW CUSTOMER/PET
            try {
                PreparedStatement pst = DBConnection.getConn().prepareStatement("INSERT INTO appointment "
                        + "(customerId, barberId, petId, start, end, description, type, createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");

                pst.setString(1, existCustomerId);
                pst.setString(2, sBarber);
                pst.setString(3, existPetId);

                pst.setTimestamp(4, startsql);
                pst.setTimestamp(5, endsql);

                pst.setString(6, sDesc);
                pst.setString(7, sType);
                pst.setString(8, savedUser);
                pst.setString(9, savedUser);

                int result = pst.executeUpdate();
                if (result == 1) {//one row was affected; namely the one that was inserted!
                    System.out.println("YAY! New Appointment Save");

                } else {
                    System.out.println("BOO! New Appointment Save");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            System.out.println("Printing  new appointment for new customer: "
                    //                + " Title " + sTitle + " "
                    + " Desc " + sDesc + " "
                    + " Type " + sType + " "
                    + " Barber " + sBarber + " " + comboBarber.getValue().nameProperty().get()
                    + " Customer " + newCustomerId + " " + txtNewCustomer.getText()
                    + " Pet " + newPetId + " " + txtNewPet.getText()
            );

        }

        mainApp.refreshView();
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
            System.out.println("Validating "
                    //                    + "Title: " + name + "\n"
                    + "Appointment TIme: " + date + " from " + start + " to " + end + "\n"
                    + "Type: " + type + "\n"
                    + "Barber: " + barber.getBarberName() + "\n"
                    + "Pet: " + pet.getPetName() + "\n"
                    + "Customer: " + customer.getCustomerName() + "\n"
            );

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

    public Boolean validateApptOverlap(String sDate, String eDate, Barber b) {
        System.out.println("Validating appointment times.. ");
        Boolean overlap = false;

        System.out.println("Timestamp WHERE " + sDate + " between start and end or " + eDate
                + "\n between start and end or " + sDate + " < start and " + eDate + " with barberId " + b.getBarberId()
        );
//        System.out.println("Timestamp " + sDate.toLocalDateTime());

//Appointment start and end time can't overlap for same barber
        try {
            PreparedStatement ps = DBConnection.getConn().prepareStatement(
                    "SELECT * FROM appointment "
                    + "WHERE (? BETWEEN start AND end AND barberId = ? "
                    + "OR ? BETWEEN start AND end AND barberId = ? "
                    + "OR ? < start AND ? > end AND barberId = ?) "
            );
            ps.setString(1, sDate);
            ps.setString(2, b.getBarberId());
            ps.setString(3, eDate);
            ps.setString(4, b.getBarberId());
            ps.setString(5, sDate);
            ps.setString(6, eDate);
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
