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

    //USED FOR APPOINTMENT ADD
    public ObservableList<Pet> getPetsByCustomer(String customerId) {

        ObservableList<Pet> petList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = DBConnection.getConn().prepareStatement(
                    "SELECT pet.petId, petName, petType, petDescription, pet.active "
                    + "FROM pet, customer "
                    + "WHERE pet.customerId = customer.customerId "
                    + "AND customer.customerId = " + customerId
            );

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("petId");
                String name = rs.getString("petName");
                String type = rs.getString("petType");
                String desc = rs.getString("petDescription");
                String active = rs.getString("pet.active");

                petList.add(new Pet(id, name, type, desc, active, customerId));

            }

        } catch (SQLException sqe) {
            System.out.println("Check SQL Exception with getting pets by customer");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Check Exception");
        }
//        comboPet.setItems(petList);
        return petList;

    }
//    public ObservableList<Pet> loadCustomerPetData() {
//
//        //adding customer notes and active to this might it not work, so make sure it's exactly
//        ObservableList<Pet> customerPetList = FXCollections.observableArrayList();
//
//        try {
//            PreparedStatement ps = DBConnection.getConn().prepareStatement(
//                    "SELECT pet.petId, pet.petName, pet.petType, pet.petDescription, pet.active, pet.customerId, "
//                    + "customer.customerId, customer.customerName, customer.customerPhone, customer.customerEmail, customer.notes, customer.active "
//                    + "FROM pet, customer "
//                    + "WHERE pet.customerId = customer.customerId "
//            );
//
//            System.out.println("running PetDAO  loadCustomerPetData()");
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                String id = rs.getString("pet.petId");
//                String name = rs.getString("pet.petName");
//                String type = rs.getString("pet.petType");
//                String desc = rs.getString("pet.petDescription");
//                String active = rs.getString("pet.active");
//
//                String cId = rs.getString("customer.customerId");
//                String cName = rs.getString("customer.customerName");
//                String cPhone = rs.getString("customer.customerPhone");
//                String cEmail = rs.getString("customer.customerEmail");
//                String cActive = rs.getString("customer.active");
//                String cNotes = rs.getString("customer.notes");
//
//                Customer c = new Customer(cId, cName, cPhone, cEmail, cActive, cNotes);
//                customerPetList.add(new Pet(id, name, type, desc, active, c));
//
//            }
//
//        } catch (SQLException sqe) {
//            System.out.println("Check SQL Exception with add customers2");
//            sqe.printStackTrace();
//        } catch (Exception e) {
//            System.out.println("Check Exception");
//        }
//        return customerPetList;
//
//    }
//
//    public ObservableList<Pet> getPetsByCustomer(int customerId) {
//
//        ObservableList<Pet> petList = FXCollections.observableArrayList();
//
//        try {
//            PreparedStatement ps = DBConnection.getConn().prepareStatement(
//                    "SELECT customer.customerId, pet.petId, petName, petType, petDescription "
//                    + "FROM customer, pet "
//                    + "WHERE customer.petId = pet.petId "
//                    + "AND customerId = " + customerId
//            );
//
//            System.out.println(ps);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                int id = rs.getInt("petId");
//                String name = rs.getString("petName");
//                String type = rs.getString("petType");
//                String desc = rs.getString("petDescription");
//
//                petList.add(new Pet(id, name, type, desc));
//
//            }
//
//        } catch (SQLException sqe) {
//            System.out.println("Check SQL Exception with getting pets by customer");
//            sqe.printStackTrace();
//        } catch (Exception e) {
//            System.out.println("Check Exception");
//        }
//        return petList;
//
//    }

    public ObservableList<Pet> loadPetData() {

        ObservableList<Pet> petList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = DBConnection.getConn().prepareStatement(
                    "SELECT petId, petName, petType, petDescription, active, customerId "
                    + "FROM pet");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("petId");
                String name = rs.getString("petName");
                String type = rs.getString("petType");
                String desc = rs.getString("petDescription");
                String active = rs.getString("active");
                String customerId = rs.getString("customerId");
                petList.add(new Pet(id, name, type, desc, active, customerId));

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
