/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.dao;

import java.sql.Connection;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Jen
 */
public class DBConnectionTest {

    @InjectMocks
    private DBConnection dbConnection;
    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMockDBConnection() throws Exception {
        Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockConnection.createStatement().executeUpdate(Mockito.any())).thenReturn(1);
        int value = dbConnection.executeQuery("");
        Assert.assertEquals(value, 1);
        Mockito.verify(mockConnection.createStatement(), Mockito.times(1));
    }

//
//    public DBConnectionTest(String name) {
//        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.cj.jdbc.Driver");
//        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://52.206.157.109/U05NQU");
//        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "U05NQU");
//        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "53688552114");
//        // System.setProperty( PropertiesBasedJdbcDatabaseTester.DBUNIT_SCHEMA, "" );
//    }
//
//    protected IDataSet getDataSet() throws Exception {
//        return new FlatXmlDataSetBuilder().build(new FileInputStream("dataset.xml"));
//    }
//                 private static final String databaseName = "U05NQU";
//    private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + databaseName;
//    private static final String username = "U05NQU";
//    private static final String password = "53688552114";
//    private static final String driver = "com.mysql.cj.jdbc.Driver";
}
