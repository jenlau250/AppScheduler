/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.cache;

import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Barber;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Jen
 */
public class AppointmentCacheTest {

    public AppointmentCacheTest() {
    }

    AppointmentCache appointmentCache = new AppointmentCache();
    private Appointment initialAppt;
    private Appointment testedAppt;
    private Barber b = new Barber("1", "Sam");
    private ArrayList<Appointment> appointmentsForBarber = new ArrayList<>();

    public void setup() {

        //Add first appointment for Sam on 1/1/20 10:00 to 11:30 am
        initialAppt = new Appointment();
        initialAppt.setBarber(b);
        initialAppt.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 10, 00));
        initialAppt.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 11, 30));
        appointmentsForBarber.add(initialAppt);

        AppointmentCache.TESTING = appointmentsForBarber;
    }

    /**
     * Test of checkAppointmentOverlap method, of class AppointmentCache.
     */
    @Test
    public void sameApptShouldNotOverlap() {

        //Add initial appointment time to list for Sam on 1/1/20 at 10:00 - 11:30am
        setup();

        Boolean result = appointmentCache.checkAppointmentOverlap(initialAppt);
        Boolean expResult = false;
        //should not overlap. no other appointments for barber yet.
        assertEquals(expResult, result);
        System.out.println("Expected result for sameApptShouldNotOverlap is false (no overlap). Actual result: " + result);

    }

    @Test
    public void earlierApptShouldNotOverlap() {

        //Add initial appointment time to list for Sam on 1/1/20 at 10:00 - 11:30am
        setup();

//     Add another appointment for Sam on 1/1/20 9am to 9:30am
        testedAppt = new Appointment();
        testedAppt.setBarber(b);
        testedAppt.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 9, 00));
        testedAppt.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 9, 30));
        appointmentsForBarber.add(testedAppt);

        AppointmentCache.TESTING = appointmentsForBarber;

        Boolean result = appointmentCache.checkAppointmentOverlap(testedAppt);
        Boolean expResult = false;
        //Checking if 9 to 9:30am overlaps with initial appointment time of 10 - 11:30am
        //Expecting false for no overlap.

        assertEquals(expResult, result);

        System.out.println("Expected result for earlierApptShouldNotOverlap is false (no overlap). Actual result: " + result);
    }

    @Test
    public void apptShouldOverlap() {
        //Add initial appointment time to list for Sam on 1/1/20 at 10:00 - 11:30am
        setup();
        testedAppt = new Appointment();
        testedAppt.setBarber(b);
        testedAppt.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 10, 15));
        testedAppt.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 12, 00));
        appointmentsForBarber.add(testedAppt);
        AppointmentCache.TESTING = appointmentsForBarber;

        Boolean result = appointmentCache.checkAppointmentOverlap(testedAppt);
        Boolean expResult = true;
        //should overlap with initial appointment
        assertEquals(expResult, result);

        System.out.println("Expected result for apptShouldOverlap is true (overlaps with initial appt). Actual result: " + result);
    }

    @Test
    public void adjacentApptShouldNotOverlap() {

        //Add initial appointment time to list for Sam on 1/1/20 at 10:00 - 11:30am
        setup();

//     Add another appointment for Sam on 1/1/20 10:45 to 11:45 am
        testedAppt = new Appointment();
        testedAppt.setBarber(b);
        testedAppt.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 9, 30));
        testedAppt.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 10, 00));
        appointmentsForBarber.add(testedAppt);
        AppointmentCache.TESTING = appointmentsForBarber;

        Boolean result = appointmentCache.checkAppointmentOverlap(testedAppt);

        Boolean expResult = false;
        //should not overlap with initial appointment
        assertEquals(expResult, result);

        System.out.println("Expected result for adjacentApptShouldNotOverlap is false (no overlap). Actual result: " + result);
    }
}
