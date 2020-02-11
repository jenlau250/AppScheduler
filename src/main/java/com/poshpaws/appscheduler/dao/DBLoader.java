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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jen
 */
public class DBLoader {

    public ObservableList<Appointment> loadApptData() {

        ObservableList<Appointment> apptList = FXCollections.observableArrayList();

        try {
            DBHandler.init();
            PreparedStatement ps = DBHandler.getConn().prepareStatement(
                    "SELECT * FROM appointment, barber, customer, pet "
                    + "WHERE appointment.barberId = barber.barberId "
                    + "AND appointment.customerId = customer.customerId "
                    + "AND appointment.petId = pet.petId "
                    + "ORDER BY `start`");

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
            DBHandler.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Check Exception");
        }
        return apptList;

    }

    public ObservableList<Barber> loadBarberData() {

        ObservableList<Barber> barberList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = DBHandler.getConn().prepareStatement(
                    "SELECT barberId, barberName, barberPhone, barberEmail, active, notes, hireDate "
                    + "FROM barber");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("barberId");
                String name = rs.getString("barberName");
                String phone = rs.getString("barberPhone");
                String email = rs.getString("barberEmail");
                Boolean active = rs.getBoolean("active");
                String notes = rs.getString("notes");
//                String hireDate = rs.getString("hireDate");

                LocalDate hireDate = rs.getObject("hireDate", LocalDate.class);
                barberList.add(new Barber(id, name, phone, email, active, notes, hireDate));

            }

        } catch (SQLException sqe) {
            System.out.println("Check SQL Exception with Barbers DAO");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Check Exception");
        }
        return barberList;

    }

    public ObservableList<Customer> loadCustomerData() {

        //adding customer notes and active to this might it not work, so make sure it's exactly
        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = DBHandler.getConn().prepareStatement(
                    "SELECT * FROM customer");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("customerId");
                String name = rs.getString("customerName");
                String phone = rs.getString("customerPhone");
                String email = rs.getString("customerEmail");
                Boolean active = rs.getBoolean("active");
                String notes = rs.getString("notes");

                Customer c = new Customer(id, name, phone, email, active, notes);

                customerList.add(c);

            }

        } catch (SQLException sqe) {
            System.out.println("Check SQL Exception with add customers2");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Check Exception");
        }
        return customerList;

    }

    public ObservableList<Pet> loadPetData() {

        ObservableList<Pet> petList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = DBHandler.getConn().prepareStatement(
                    "SELECT petId, petName, petType, petDescription, customerId "
                    + "FROM pet");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("petId");
                String name = rs.getString("petName");
                String type = rs.getString("petType");
                String desc = rs.getString("petDescription");
                String customerId = rs.getString("customerId");
                petList.add(new Pet(id, name, type, desc, customerId));

            }

        } catch (SQLException sqe) {
            System.out.println("Check SQL Exception with pet dao impl");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Check Exception");
        }
        return petList;

    }

}
