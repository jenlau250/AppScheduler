/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.cache;

import com.poshpaws.appscheduler.dao.DBLoader;
import com.poshpaws.appscheduler.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jen
 */
public class CustomerCache {

    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();

    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> returnList = FXCollections.observableArrayList();
        returnList.addAll(customerList);
        return returnList;
    }

    public static ObservableList<Customer> getAllActiveCustomers() {
        ObservableList<Customer> returnList = FXCollections.observableArrayList();
        for (Customer c : customerList) {

            if (c.getActive()) {
                returnList.add(c);
            }
        }

        return returnList;

    }

    public static ObservableList<Customer> getAllInactiveCustomers() {
        ObservableList<Customer> returnList = FXCollections.observableArrayList();
        for (Customer c : customerList) {

            if (!c.getActive()) {
                returnList.add(c);
            }
        }

        return returnList;

    }

    public static Customer getCustomer(String customerId) {
        for (Customer c : customerList) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
            }
        }
        return null;
    }

    public static void flush() {
        customerList.clear();

        customerList.addAll(new DBLoader().loadCustomerData());

    }

}
