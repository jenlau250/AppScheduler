package com.poshpaws.appscheduler.util;

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package jCalendar.utilities;
//
//import jCalendar.model.Customer;
//import javafx.util.StringConverter;
//
///**
// *
// * @author Jen
// */
//public class CustomerConverter extends StringConverter<Customer> {
//
//    //Method to convert a Customer Object to a String
//    @Override
//    public String toString(Customer customer) {
//        return customer == null ? null : customer.customerNameProperty().get();
//    }
////
////    //Method to convert a String to a Customer
////    @Override
////    public Customer fromString(String string) {
////        Customer customer = null;
////
////        if (string == null) {
////            return customer;
////        }
////
////        //this splits a string delimited by ,
////        int commaIndex = string.indexOf(",");
////
////        int id = Integer.parseInt(string.substring(commaIndex + 2));
////        String name = string.substring(0, commaIndex);
////
////        customer = new Customer(id, name);
////
////        return customer;
////
////    }
//}
