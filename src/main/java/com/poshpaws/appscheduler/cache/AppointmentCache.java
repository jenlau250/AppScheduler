/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.cache;

import com.poshpaws.appscheduler.dao.DBLoader;
import com.poshpaws.appscheduler.model.Appointment;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jen
 */
public class AppointmentCache {

    public static final AppointmentCache SINGLETON = new AppointmentCache();
    public static ArrayList<Appointment> TESTING = null;

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> returnList = FXCollections.observableArrayList();
        returnList.addAll(appointmentList);
        return returnList;
    }

    public static List<Appointment> getBarberAppointmentsTest(String barberId) {

        if (TESTING == null) {
            return getBarberAppointments(barberId);
        } else {
            return TESTING;
        }

    }

    public static List<Appointment> getBarberAppointments(String barberId) {

        List<Appointment> returnList = new ArrayList<>();

        for (Appointment a : appointmentList) {
            if (a.getBarber().getBarberId().equals(barberId)) {
                returnList.add(a);
            }
        }

        return returnList;

    }

    public static Boolean checkAppointmentOverlap(Appointment a) {
        boolean overlap = false;
        System.out.println("appointment testing " + a);

        List<Appointment> appointmentsForBarber = new ArrayList<>();
        appointmentsForBarber = getBarberAppointmentsTest(a.getBarber().getBarberId());

        //This is to exclude the appointment being updated from comparing to itself
        appointmentsForBarber.remove(a);
        for (Appointment other : appointmentsForBarber) {
            System.out.println("Other appt checking" + other);
            if (a.getStart().isBefore(other.getEnd()) && other.getStart().isBefore(a.getEnd())) {
                overlap = true;
            }
        }
        //Add back after validation
        appointmentsForBarber.add(a);

        return overlap;

    }

    public static Appointment getAppointment(String appointmentId) {
        for (Appointment a : appointmentList) {
            if (a.getAppointmentId().equals(appointmentId)) {
                return a;
            }
        }
        return null;
    }

    public static void flush() {
        appointmentList.clear();
        appointmentList.addAll(new DBLoader().loadApptData());

    }
}
