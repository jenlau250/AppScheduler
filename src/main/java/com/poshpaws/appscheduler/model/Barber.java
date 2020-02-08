/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.model;

import java.time.LocalDate;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author jlau2
 */
public class Barber {

    private final StringProperty barberId = new SimpleStringProperty();
    private final StringProperty barberName = new SimpleStringProperty();
    private final StringProperty barberPhone = new SimpleStringProperty();
    private final StringProperty barberEmail = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final SimpleBooleanProperty active = new SimpleBooleanProperty();
    private final ObjectProperty<LocalDate> hireDate = new SimpleObjectProperty();

    public Barber() {

    }

    //FOR TABLEVIEW
    public Barber(String id, String name, String phone, String email, Boolean active, String notes, LocalDate date) {
        this.barberId.set(id);
        this.barberName.set(name);
        this.barberPhone.set(name);
        this.barberEmail.set(name);

        this.active.set(active);
        this.notes.set(notes);
        this.hireDate.set(date);
    }

    public StringProperty nameProperty() {
        return barberName;
    }

    public String getBarberName() {
        return barberName.get();
    }

    public void setBarberName(String barberName) {
        this.barberName.set(barberName);
    }

    public StringProperty barberPhoneProperty() {
        return barberPhone;
    }

    public StringProperty barberEmailProperty() {
        return barberEmail;
    }

    public StringProperty noteProperty() {
        return notes;
    }

    public Boolean getActive() {
        return active.get();
    }

    public void setActive(Boolean active) {
        this.active.set(active);
    }

    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    public StringProperty barberIdProperty() {
        return barberId;
    }

    public Barber(String barberId, String name) {
        this.barberId.set(barberId);
        this.barberName.set(name);
    }

    public String getPhone() {
        return barberPhone.get();
    }

    public void setPhone(String phone) {
        this.barberPhone.set(phone);
    }

    public String getEmail() {
        return barberEmail.get();
    }

    public void setEmail(String email) {
        this.barberEmail.set(email);

    }

    public String getBarberId() {
        return barberId.get();
    }

    public void setBarberId(String barberId) {
        this.barberId.set(barberId);
    }

    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public ObjectProperty hireDateProperty() {
        return hireDate;
    }

    public LocalDate getHireDate() {
        return hireDate.get();
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate.setValue(hireDate);
    }

    @Override
    public String toString() {
        return String.valueOf(barberName.get());
    }

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o) {
            return true;
        } else // null check
        if (o == null) {
            return false;
        } else // type check and cast
        if (getClass() != o.getClass()) {
            return false;
        } else {
            final Barber a = (Barber) o;
            // field comparison
            return Objects.equals(a, a);
        }
    }

}
