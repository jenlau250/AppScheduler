/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.dao;

import com.poshpaws.appscheduler.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author jlau2
 */
/* */
public class UserDaoImpl {

    public static User getUser(String userName) throws SQLException, Exception {

        DBConnection.init();

        String sqlStatement = "select * FROM user WHERE userName  = '" + userName + "'";
        Query.makeQuery(sqlStatement);

        User userResult;
        ResultSet result = Query.getResult();
        while (result.next()) {
            int userid = result.getInt("userid");
            String userNameG = result.getString("userName");
            String password = result.getString("password");
            userResult = new User(userid, userName, password);
            return userResult;
        }
        DBConnection.closeConnection();
        return null;
    }

    public static ObservableList<User> getAllUsers() throws SQLException, Exception {
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        DBConnection.init();
        String sqlStatement = "select * from user";
        Query.makeQuery(sqlStatement);
        ResultSet result = Query.getResult();
        while (result.next()) {
            int userid = result.getInt("userid");
            String userNameG = result.getString("userName");
            String password = result.getString("password");
            User userResult = new User(userid, userNameG, password);
            allUsers.add(userResult);

        }
        DBConnection.closeConnection();
        return allUsers;
    }

}
