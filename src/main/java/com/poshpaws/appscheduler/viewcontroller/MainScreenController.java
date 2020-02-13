/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.poshpaws.appscheduler.AppScheduler;
import com.poshpaws.appscheduler.model.User;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.io.FileNotFoundException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jlau2
 */
public class MainScreenController {

    private AppScheduler mainApp;
    private User currentUser;

    @FXML
    private JFXButton btnAppt;
    @FXML
    private JFXButton btnCustomers;
    @FXML
    private JFXButton btnStaff;
    @FXML
    private JFXButton btnReports;
    @FXML
    private JFXButton btnExit;

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());
    private final ZoneId newzid = ZoneId.systemDefault();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    public MainScreenController() {

    }

    /**
     * Initializes Menu
     *
     * @param mainApp
     * @param currentUser
     */
    public void setMenu(AppScheduler mainApp, User currentUser) throws FileNotFoundException {
        this.mainApp = mainApp;
        this.currentUser = currentUser;

        ((Stage) (btnCustomers.getScene().getWindow())).centerOnScreen();
//        reminderList = FXCollections.observableArrayList();
//
////        populateReminderList();
//        reminder();

        btnCustomers.setOnAction((evt) -> {
            this.mainApp.showCustomerScreen();
        });

        btnStaff.setOnAction((evt) -> {
            this.mainApp.showBarberScreen();
        });
        btnAppt.setOnAction((evt) -> {
            this.mainApp.showAppointmentListScreen();
//            this.mainApp.showAppointmentScreen(this.currentUser);
        });

        btnReports.setOnAction((evt) -> {
            this.mainApp.showReportScreen();
        });
        btnExit.setOnAction((evt) -> {
            this.mainApp.showLoginScreen();
        });

    }

//    private void reminder() {
//
//        LocalDateTime now = LocalDateTime.now();
//
//        //Set alert for appointments within 15 minutes of current time
//        LocalDateTime nowPlus15Min = now.plusMinutes(1550);
//
//        FilteredList<Appointment> filteredData = new FilteredList<>(reminderList);
//
//        filteredData.setPredicate(row -> {
//            LocalDateTime rowDate = row.getStart();
////            LocalDateTime rowDate = LocalDateTime.parse(row.getStart(), timeDTF);
//            return rowDate.isAfter(now.minusMinutes(1)) && rowDate.isBefore(nowPlus15Min);
//        });
//
//        if (filteredData.isEmpty()) {
//            txtAreaReminders.setText("No Upcoming Appointments within 15 minutes");
//        } else {
//            String type = filteredData.get(0).getType();
//            String customer = filteredData.get(0).getCustomer().getCustomerName();
//            String start = filteredData.get(0).getStart().toString();
//
//            txtAreaReminders.appendText("Reminder for upcoming appointment! \n"
//                    + " Customer: " + customer + "\n"
//                    + " Start Time: " + start + "\n"
//                    + " Appointment Type: " + type
//            );
//        }
//    }
//    private void populateReminderList() {
//
//        try {
//
//            PreparedStatement ps = DBHandler.getConn().prepareStatement(
//                    "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, appointment.location, "
//                    + "appointment.`start`, appointment.`end`, customer.customerId, customer.customerName, appointment.createdBy "
//                    + "FROM appointment, customer "
//                    + "WHERE appointment.customerId = customer.customerId AND appointment.createdBy = ? "
//                    + "ORDER BY `start`");
//            ps.setString(1, currentUser.getUserName());
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//
//                Timestamp pStart = rs.getTimestamp("appointment.start");
//                Timestamp pEnd = rs.getTimestamp("appointment.end");
//                ZonedDateTime newZoneStart = pStart.toLocalDateTime().atZone(ZoneId.of("UTC"));
//                ZonedDateTime newZoneEnd = pEnd.toLocalDateTime().atZone(ZoneId.of("UTC"));
//                ZonedDateTime newLocalStart = newZoneStart.withZoneSameInstant(newzid);
//                ZonedDateTime newLocalEnd = newZoneEnd.withZoneSameInstant(newzid);
//
//                String pAppointmentId = rs.getString("appointment.appointmentId");
//                String pTitle = rs.getString("appointment.title");
//                String pType = rs.getString("appointment.description");
//                String pLocation = rs.getString("appointment.location");
//                Customer pCustomer = new Customer(rs.getInt("appointment.customerId"), rs.getString("customer.customerName"));
//                String pUser = rs.getString("appointment.createdBy");
//
//                reminderList.add(new Appointment(pAppointmentId, newLocalStart.format(timeDTF), newLocalEnd.format(timeDTF), pTitle, pType, pLocation, pCustomer, pUser));
//            }
//        } catch (SQLException sqe) {
//            logger.log(Level.SEVERE, "SQL Exception with showing reminder data.");
//            sqe.printStackTrace();
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, "Check Exception Error");
//            e.printStackTrace();
//        }
//    }
//
}
