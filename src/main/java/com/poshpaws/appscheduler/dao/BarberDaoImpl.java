/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.dao;

import com.poshpaws.appscheduler.model.Barber;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Jen
 */
public class BarberDaoImpl {

    public ObservableList<Barber> loadBarberData() {

        ObservableList<Barber> barberList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = DBConnection.getConn().prepareStatement(
                    "SELECT barberId, barberName, barberPhone, barberEmail, active, notes, hireDate "
                    + "FROM barber");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String id = rs.getString("barberId");
                String name = rs.getString("barberName");
                String phone = rs.getString("barberPhone");
                String email = rs.getString("barberEmail");
                Boolean active = rs.getBoolean("active");
                String notes = rs.getString("notes");
//                String hireDate = rs.getString("hireDate");

                LocalDate hireDate = rs.getObject("hireDate", LocalDate.class);
                barberList.add(new Barber(id, name, phone, email, active, notes, hireDate));

            }

        } catch (SQLException sqe) {
            System.out.println("Check SQL Exception with Barbers DAO");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Check Exception");
        }
        return barberList;

    }
}
