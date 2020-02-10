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
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.prefs.Preferences;

/**
 *
 * @author jlau2
 */
public final class DBHandler {

    private static DBHandler handler = null;

    private static final String databaseName = "U05NQU";
    private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + databaseName;
    private static final String username = "U05NQU";
    private static final String password = "53688552114";
    private static final String driver = "com.mysql.cj.jdbc.Driver";

    private static Connection conn = null;
    private static Statement stmt = null;
    private static String savedUser;

    //Default terms
    private static String[] barbers = {"Sam", "Joe", "Elisa"};

    private static boolean tablesExist = false;

    static {
        init();
        //get user ID
        String userName = "";
        Preferences userPreferences = Preferences.userRoot();
        savedUser = userPreferences.get("username", userName);

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

    /**
     * Updates customer records
     */
    public static boolean updateCustomer(Customer c) {

        try {

            PreparedStatement pst = DBHandler.getConn().prepareStatement("UPDATE customer "
                    + "SET customerName= ?, customerPhone = ?, customerEmail=?, notes=?, active=?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
                    + "WHERE customerId = ? ");
            pst.setString(1, c.getCustomerName());
            pst.setString(2, c.getPhone());
            pst.setString(3, c.getEmail());
            pst.setString(4, c.getNotes());
            pst.setBoolean(5, c.getActive());
            pst.setString(6, savedUser);
            pst.setString(7, c.getCustomerId());

            int res = pst.executeUpdate();
            if (res == 1) {
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Inserts new customer record
     */
    public static boolean addNewCustomer(Customer c, Pet p) {

        try {

            PreparedStatement pst = DBHandler.getConn().prepareStatement("INSERT INTO customer "
                    + "( customerName, customerPhone, customerEmail, notes, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + " VALUES ( ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, c.getCustomerName());
            pst.setString(2, c.getPhone());
            pst.setString(3, c.getEmail());
            pst.setString(4, c.getNotes());
            pst.setBoolean(5, c.getActive());
            pst.setString(6, savedUser);
            pst.setString(7, savedUser);

            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();

            int newPetId = -1;
            int newCustomerId = -1;

            if (rs.next()) {
                newPetId = rs.getInt(1);
                newCustomerId = rs.getInt(1);
            }

            PreparedStatement ps = DBHandler.getConn().prepareStatement("INSERT INTO pet "
                    + "(petName, petType, petDescription, createDate, createdBy, lastUpdate, lastUpdateBy, customerId) "
                    + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?)");
            ps.setString(1, p.getPetName());
            ps.setString(2, p.getPetType());
            ps.setString(3, p.getPetDesc());
            ps.setString(4, savedUser);
            ps.setString(5, savedUser);
            ps.setInt(6, newCustomerId);

//            ps.executeUpdate();
            int res = ps.executeUpdate();
            if (res == 1) {
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Inserts new pet record
     */
    public static boolean addNewPet(Pet p) {

        try {

            PreparedStatement ps = DBHandler.getConn().prepareStatement("INSERT INTO pet "
                    + "(petName, petType, petDescription, createDate, createdBy, lastUpdate, lastUpdateBy, customerId) "
                    + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?)");
            ps.setString(1, p.getPetName());
            ps.setString(2, p.getPetType());
            ps.setString(3, p.getPetDesc());
            ps.setString(4, savedUser);
            ps.setString(5, savedUser);
            ps.setString(6, p.getCustomerId());

//            ps.executeUpdate();
            int res = ps.executeUpdate();
            if (res == 1) {
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Updates pet record
     */
    public static boolean updatePet(Pet p) {

        try {

            PreparedStatement pst = DBHandler.getConn().prepareStatement("UPDATE pet "
                    + "SET petName=?, petType=?, petDescription=?, lastUpdate=CURRENT_TIMESTAMP, lastUpdateBy=? "
                    + "WHERE petId = ? ");
            pst.setString(1, p.getPetName());
            pst.setString(2, p.getPetType());
            pst.setString(3, p.getPetDesc());
            pst.setString(4, savedUser);
            pst.setString(5, p.getPetId());
            int res = pst.executeUpdate();
            if (res == 1) {
                return true;
            };

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean deleteCustomer(Customer customer) {

        try {

            PreparedStatement pst = DBHandler.getConn().prepareStatement("DELETE from pet WHERE pet.customerId = ?");
            pst.setString(1, customer.getCustomerId());
            int res1 = pst.executeUpdate();

            PreparedStatement ps2 = DBHandler.getConn().prepareStatement("DELETE from customer WHERE customer.customerId = ?");
            ps2.setString(1, customer.getCustomerId());
            ps2.executeUpdate();

            int res = ps2.executeUpdate();
            if (res1 == 1) {
                return true;
            };

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static boolean deletePet(Pet p) {

        try {
            PreparedStatement pst = DBHandler.getConn().prepareStatement("DELETE FROM images WHERE petId = ?");
            pst.setString(1, p.getPetId());
            pst.executeUpdate();

            PreparedStatement ps2 = DBHandler.getConn().prepareStatement("DELETE FROM pet WHERE petId = ?");
            ps2.setString(1, p.getPetId());
//            ps2.executeUpdate();
            int res = ps2.executeUpdate();
            if (res == 1) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
