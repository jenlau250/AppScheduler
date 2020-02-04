/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.cache;

import com.poshpaws.appscheduler.dao.AppointmentDaoImpl;
import com.poshpaws.appscheduler.model.Appointment;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jen
 */
public class AppointmentCache {

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> returnList = FXCollections.observableArrayList();
        returnList.addAll(appointmentList);
        return returnList;
    }

    public static ObservableList<Appointment> getBarberAppointments(int barberId) {

        ObservableList<Appointment> returnList = FXCollections.observableArrayList();

        for (Appointment a : appointmentList) {
            if (a.getBarber().getBarberId().equals(barberId)) {
                returnList.add(a);
            }
        }

        return returnList;

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
