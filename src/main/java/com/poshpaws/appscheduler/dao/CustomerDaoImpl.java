/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.dao;

import com.poshpaws.appscheduler.model.Customer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jlau2
 */
public class CustomerDaoImpl {

    private static String savedUser;

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

}
