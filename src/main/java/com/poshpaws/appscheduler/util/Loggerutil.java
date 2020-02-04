/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.util;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author jlau2
 */
public class Loggerutil {

    private static final Logger LOGGER = Logger.getLogger(Loggerutil.class.getName());

    public static void init() {

	Handler consoleHandler = null;
	Handler fileHandler = null;
	
	try {
	    //Creating consoleHandler and fileHandler
	    consoleHandler = new ConsoleHandler();
	    fileHandler = new FileHandler("./log.txt", true);

	    //Assigning handlers to LOGGER object
	    LOGGER.addHandler(consoleHandler);
	    LOGGER.addHandler(fileHandler);

	    //Setting levels to handlers and LOGGER
	    consoleHandler.setLevel(Level.ALL);
	    fileHandler.setLevel(Level.ALL);
	    fileHandler.setFormatter(new SimpleFormatter());
	    
	    LOGGER.setLevel(Level.ALL);
	    //Console handler removed
	    LOGGER.removeHandler(consoleHandler);

	} catch (IOException exception) {
	    LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
	}
    }
    
    public static String currentTimestamp() {
    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    return f.format(c.getTime());
}
}
