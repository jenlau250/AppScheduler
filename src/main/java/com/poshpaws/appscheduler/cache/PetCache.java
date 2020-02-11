/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.cache;

import com.poshpaws.appscheduler.dao.DBLoader;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jen
 */
public class PetCache {

    private static ObservableList<Pet> petList = FXCollections.observableArrayList();

    public static ObservableList<Pet> getAllPets() {
        ObservableList<Pet> returnList = FXCollections.observableArrayList();
        returnList.addAll(petList);
        return returnList;
    }

    public static Pet getPet(String petId) {
        for (Pet p : petList) {
            if (p.getPetId().equals(petId)) {
                return p;
            }
        }
        return null;
    }

    public static void flush() {
        petList.clear();
        petList.addAll(new DBLoader().loadPetData());
    }

    public static ObservableList<Pet> getSelectedPets(Customer c) {

        ObservableList<Pet> returnList = FXCollections.observableArrayList();
        for (Pet p : petList) {
            if (p.getCustomerId().equals(c.getCustomerId())) {
                returnList.addAll(p);
            }

        }
        return returnList;

    }

}
