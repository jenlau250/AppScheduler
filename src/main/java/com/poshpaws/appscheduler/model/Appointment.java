/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.model;

import com.poshpaws.appscheduler.util.DateTimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jlau2
 */
public class Appointment {

    private final StringProperty appointmentId = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty();
    private final ObjectProperty<LocalDateTime> start = new SimpleObjectProperty();
    private final ObjectProperty<LocalDateTime> end = new SimpleObjectProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty(); //type
    private ObjectProperty<Pet> pet = new SimpleObjectProperty<>();
    private ObjectProperty<Customer> customer = new SimpleObjectProperty<>();
    private ObjectProperty<Barber> barber = new SimpleObjectProperty<>();
    private String month;
    private String count;

    private static final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private static final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private static ObservableList<String> apptTypes = FXCollections.observableArrayList("Bath & Haircut", "Bath & Brush", "Haircut ", "Bath", "Brush", "Daycare", "Other");

    public Appointment() {
    }

    public Appointment(String appointmentId, LocalDate startDate, LocalDateTime start, LocalDateTime end, String desc, String type, Barber barber, Customer customer, Pet pet) {

        this.appointmentId.set(appointmentId);
        this.startDate.set(startDate);
        this.start.set(start);
        this.end.set(end);
        this.description.set(desc);
        this.type.set(type);
        this.barber.set(barber);
        this.customer.set(customer);
        this.pet.set(pet);

    }

    //to save appointment
    public Appointment(LocalDateTime start, LocalDateTime end, String desc, String type, Barber barber, Customer customer, Pet pet) {
        this.start.set(start);
        this.end.set(end);
        this.description.set(desc);
        this.type.set(type);
        this.barber.set(barber);
        this.customer.set(customer);
        this.pet.set(pet);

    }

    public Appointment(String month, Barber barber, String type, String count) {
        this.month = month;
        this.barber.set(barber);
        this.type.set(type);
        this.count = count;
    }

    public String getAppointmentId() {
        return appointmentId.get();
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId.set(appointmentId);
    }

    public StringProperty appointmentIdProperty() {
        return appointmentId;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObjectProperty startProperty() {
        return start;
    }

    public LocalDateTime getStart() {
        return start.get();
    }

    public ObjectProperty endProperty() {
        return end;
    }

    public LocalDateTime getEnd() {
        return end.get();
    }

    public ObjectProperty startDateProperty() {
        return startDate;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }

    public ObjectProperty barberProperty() {
        return barber;
    }

    public ObjectProperty customerProperty() {
        return customer;
    }

    public ObjectProperty petProperty() {
        return pet;
    }

    public Customer getCustomer() {
        return customer.get();
    }

    public Barber getBarber() {
        return barber.get();
    }

    public void setBarber(Barber barber) {
        this.barber.set(barber);
    }

    public void setCustomer(Customer customer) {
        this.customer.set(customer);
    }

    public Pet getPet() {
        return this.pet.get();
    }

    public void setPet(Pet pet) {
        this.pet.set(pet);
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public static ObservableList<String> getApptTypes() {
        return apptTypes;
    }

    public static ObservableList<String> getDefaultStartTimes() {
        //Set time from 8AM to 5PM in 15 minute increments
        LocalTime appointmentStartTime = LocalTime.of(7, 0);
        while (!appointmentStartTime.equals(LocalTime.of(17, 0))) {
            String startTime = DateTimeUtil.parseTimeToStringFormat(appointmentStartTime);
            startTimes.add(startTime);
            appointmentStartTime = appointmentStartTime.plusMinutes(15);

        }
        return startTimes;
    }

    public static ObservableList<String> getDefaultEndTimes() {
        //Set time from 7AM to 5PM in 15 minute increments
        LocalTime appointmentEndTime = LocalTime.of(7, 15);
        while (!appointmentEndTime.equals(LocalTime.of(17, 15))) {
            String endTime = DateTimeUtil.parseTimeToStringFormat(appointmentEndTime);
            endTimes.add(endTime);
            appointmentEndTime = appointmentEndTime.plusMinutes(15);

        }
        return endTimes;
    }

    @Override
    //public Appointment(String appointmentId, String title, String startDate, LocalDateTime start, LocalDateTime end, String desc, String type, Barber barber, Customer customer, Pet pet) {

    public String toString() {
        return "ID: " + getAppointmentId() + '\n'
                + "Start Date: " + getStartDate() + '\n'
                + "Time: " + getStart() + " to " + getEnd() + '\n'
                + "Description: " + getDescription() + '\n'
                + "Type: " + getType() + '\n'
                + "Barber: " + getBarber().getBarberId() + " - " + getBarber().getBarberName() + '\n'
                + "Customer: " + getCustomer().getCustomerId() + " - " + getCustomer().getCustomerName() + '\n'
                + "Pet: " + getPet().getPetId() + " - " + getPet().getPetName() + '\n';
    }

}
