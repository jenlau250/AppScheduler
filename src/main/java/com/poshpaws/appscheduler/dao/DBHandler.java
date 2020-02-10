package com.poshpaws.appscheduler.dao;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jlau2
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author jlau2
 */
public class DBHandler {

    private static DBHandler handler = null;

    private static final String databaseName = "U05NQU";
    private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + databaseName;
    private static final String username = "U05NQU";
    private static final String password = "53688552114";
    private static final String driver = "com.mysql.cj.jdbc.Driver";

    private static Connection conn = null;
    private static Statement stmt = null;

    //Default terms
    private static String[] barbers = {"Sam", "Joe", "Elisa"};

    private static boolean tablesExist = false;

    static {
        init();
    }

    //Constructor
    private DBHandler() {

    }

    public static void init() {
        try {
            Class.forName(driver).newInstance();
            conn = (Connection) DriverManager.getConnection(DB_URL, username, password);
            System.out.println("Connection Successful");

        } catch (Exception e) {
            System.out.println("Can't load database");
        }

    }

    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception {
        conn.close();
        System.out.println("Connection Closed");
    }

    public static Connection getConn() {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(DB_URL, username, password);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("couldn't connect");
            throw new RuntimeException(ex);
        }
    }

    public int executeQuery(String query) throws ClassNotFoundException, SQLException {
        return conn.createStatement().executeUpdate(query);
    }

}
