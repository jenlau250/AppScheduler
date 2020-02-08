/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.model;

import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jlau2
 */
public class Pet {

    private final StringProperty petId = new SimpleStringProperty();
    private final StringProperty petName = new SimpleStringProperty();
    private final StringProperty petType = new SimpleStringProperty();
    private final StringProperty petDescription = new SimpleStringProperty();
    private final StringProperty active = new SimpleStringProperty();
    private final StringProperty customerId = new SimpleStringProperty();
    private String image;
    private String count;
//    private LocalDate lastSeen;
    private static ObservableList<String> petTypes = FXCollections.observableArrayList("Dog", "Puppy", "Cat", "Kitten", "Other");

    public Pet() {

    }

//    //pet report
//    public Pet(String name, LocalDate lastSeen) {
//        this.petType.set(name);
//        this.lastSeen = lastSeen;
//    }
    //pet report
    public Pet(String type, String count) {
        this.petType.set(type);
        this.count = count;
    }

    //FOR TABLEVIEW
    public Pet(String id, String name, String type, String desc, String active, String customerId) {
        this.petId.set(id);
        this.petName.set(name);
        this.petType.set(type);
        this.petDescription.set(desc);
        this.active.set(active);
        this.customerId.set(customerId);
    }

    //used for getPetsByCustomer(customerId)
    public Pet(String id, String name, String type, String desc) {
        this.petId.set(id);
        this.petName.set(name);
        this.petType.set(type);
        this.petDescription.set(desc);

    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public StringProperty nameProperty() {
        return petName;
    }

    public StringProperty typeProperty() {
        return petType;
    }

    public StringProperty descProperty() {
        return petDescription;
    }

    public StringProperty petIdProperty() {
        return petId;
    }

    public StringProperty customerIdProperty() {
        return customerId;
    }

    public StringProperty activeProperty() {
        return active;
    }

    public String getPetId() {
        return petId.get();
    }

    public String getCustomerId() {
        return customerId.get();
    }

    public String getPetName() {
        return petName.get();
    }

    public String getPetType() {
        return petType.get();
    }

    public String getPetDesc() {
        return petDescription.get();
    }

    public String getActive() {
        return active.get();
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public static ObservableList<String> getPetTypes() {
        return petTypes;
    }

//    public LocalDate getLastSeen() {
//        return lastSeen;
//    }
//
//    public void setLastSeen(LocalDate lastSeen) {
//        this.lastSeen = lastSeen;
//    }
    @Override
    public String toString() {
        return "Pet ID: " + petId + '\n'
                + "Name: " + petName + '\n'
                + "Type: " + petType + '\n'
                + "Desc: " + petDescription + '\n';
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
            final Pet a = (Pet) o;
            // field comparison
            return Objects.equals(a, a);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(petId.get(), petName.get());
    }

}
