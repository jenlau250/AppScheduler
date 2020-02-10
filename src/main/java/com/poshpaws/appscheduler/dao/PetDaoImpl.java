/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.dao;

import com.poshpaws.appscheduler.model.Pet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jen
 */
public class PetDaoImpl {

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
