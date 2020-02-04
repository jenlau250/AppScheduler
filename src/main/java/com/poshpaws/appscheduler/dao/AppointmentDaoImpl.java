/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.dao;

import com.poshpaws.appscheduler.cache.BarberCache;
import com.poshpaws.appscheduler.cache.CustomerCache;
import com.poshpaws.appscheduler.cache.PetCache;
import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Barber;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jen
 */
public class AppointmentDaoImpl {

    private static final DateTimeFormatter dateformat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

    public ObservableList<Appointment> loadApptData() {

        ObservableList<Appointment> apptList = FXCollections.observableArrayList();

        try {

            DBConnection.init();
            PreparedStatement ps = DBConnection.getConn().prepareStatement(
                    "SELECT * FROM appointment, barber, customer, pet "
                    + "WHERE appointment.barberId = barber.barberId "
                    + "AND appointment.customerId = customer.customerId "
                    + "AND appointment.petId = pet.petId");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String appointmentId = rs.getString("appointmentId");
                Timestamp startDate = rs.getTimestamp("start");
                Timestamp start = rs.getTimestamp("start");
                Timestamp end = rs.getTimestamp("end");
                String desc = rs.getString("appointment.description");
                String type = rs.getString("appointment.type");

                Barber barber = BarberCache.getBarber(rs.getString("barber.barberId"));

                Customer customer = CustomerCache.getCustomer(rs.getString("customer.customerId"));
                Pet pet = PetCache.getPet(rs.getString("pet.petId"));

                apptList.add(new Appointment(appointmentId, startDate.toLocalDateTime().toLocalDate(), start.toLocalDateTime(), end.toLocalDateTime(), desc, type, barber, customer, pet));

            }
            DBConnection.closeConnection();

        } catch (SQLException sqe) {
            System.out.println("Check SQL Exception with add Appointments 2");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Check Exception");
        }
        return apptList;

    }

}