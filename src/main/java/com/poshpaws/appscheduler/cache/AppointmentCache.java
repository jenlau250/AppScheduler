/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.cache;

import com.poshpaws.appscheduler.dao.AppointmentDaoImpl;
import com.poshpaws.appscheduler.model.Appointment;
import java.time.LocalDateTime;
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
        System.out.println("********************************************Hi********");
        boolean overlap = false;

        List<Appointment> appointmentsForBarber = new ArrayList<>();
        appointmentsForBarber = getBarberAppointmentsTest(a.getBarber().getBarberId());

//add this appointment after checking for conflicts
        appointmentsForBarber.remove(a);
        for (Appointment other : appointmentsForBarber) {
//            /t1.begin.isBefore(t2.end) && t2.begin.isBefore(t1.end)
            if (a.getStart().isBefore(other.getEnd()) && other.getStart().isBefore(a.getEnd())) {
                overlap = true;
            }
        }
        //add back after validating
        appointmentsForBarber.add(a);
        return overlap;
//        return true;
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
        appointmentList.addAll(new AppointmentDaoImpl().loadApptData());

    }

    public static ObservableList<Appointment> getBarberAppointmentByStartEndTime(int barberId, LocalDateTime start, LocalDateTime end) {

        ObservableList<Appointment> returnList = FXCollections.observableArrayList();
//use -1 trick to add All Barbers to selection

        for (Appointment a : appointmentList) {
            if (a.getBarber().getBarberId().equals(barberId)) { // if -1 or getBarber matches
                //check start and end date overlap

//                if all matches, add to list
                returnList.add(a);
            }
        }

        return returnList;

    }

//    public ObservableList<Pet> lastAppointmentforPet() {
//        ObservableList<Pet> lastDatesforPets = FXCollections.observableArrayList();
//        LocalDate currDate = LocalDate.now();
//        for (Appointment a : appointmentList) {
//            for (Pet p : PetCache.getAllPets()) {
////for all past appointment times, return all pets last seen tiem
//                if (p.equals(a.getPet())) {
//                    if (a.getStartDate().compareTo(currDate) < 0) {
//                        lastDatesforPets.add(p.getPetName(), a.getStartDate().MAX);
//                    }
//
//                }
//
//            }
//        }
//        return lastDatesforPets;
//    }
}
