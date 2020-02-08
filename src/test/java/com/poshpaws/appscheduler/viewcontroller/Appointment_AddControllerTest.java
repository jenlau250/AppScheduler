/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.poshpaws.appscheduler.cache.AppointmentCache;
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
public class Appointment_AddControllerTest {

//    Method to test
//  public Boolean checkAppointmentOverlap(LocalDateTime aStart, LocalDateTime aEnd, String barberId) {
//        boolean overlap = false;
//
//        List<Appointment> appointmentsForBarber = new ArrayList<>();
//        appointmentsForBarber = getBarberAppointmentsTest(barberId);
    //        //add this appointment after checking for conflicts
    ////        appointmentsForBarber.remove(appointment);
//        for (Appointment other : appointmentsForBarber) {
//            if (aStart.isBefore(other.getEnd()) && (aEnd.isAfter(other.getStart()))) {
//                overlap = true;
//            }
//        }
//        return overlap;
////        return true;
//    }
    AppointmentCache appointmentCache = new AppointmentCache();
    private Appointment a1;
    private Appointment a2;
    private Appointment a3;
    private Barber b = new Barber("1", "Sam");
    private ArrayList<Appointment> appointmentsForBarber = new ArrayList<>();

    public void setup() {

//Add first appointment for Sam on 1/1/20 10:00 to 11:30 am
        a1 = new Appointment();
        a1.setBarber(b);
        a1.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 10, 00));
        a1.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 11, 30));
        appointmentsForBarber.add(a1);

        AppointmentCache.TESTING = appointmentsForBarber;
    }

    /**
     * Test of validateApptOverlap method, of class Appointment_AddController.
     */
    @Test
    public void sameApptShouldNotOverlap() {

        //Add initial appointment time to list for Sam on 1/1/20 at 10:00 - 11:30am
        setup();

        Boolean result = appointmentCache.checkAppointmentOverlap(a1);
        Boolean expResult = false;
        //should not overlap. no other appointments for barber yet.
        assertEquals(expResult, result);

    }

    @Test
    public void testEarlierApptShouldNotOverlap() {

        //Add initial appointment time to list for Sam on 1/1/20 at 10:00 - 11:30am
        setup();

//     Add another appointment for Sam on 1/1/20 10:45 to 11:45 am
        a2 = new Appointment();
        a2.setBarber(b);
        a2.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 9, 00));
        a2.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 9, 30));
        appointmentsForBarber.add(a2);

        AppointmentCache.TESTING = appointmentsForBarber;

        Boolean result = appointmentCache.checkAppointmentOverlap(a2);
        Boolean expResult = false;
        //Expecting false for no overlap. Checking if 9 to 9:30am overlaps with initial appointment time of 10 - 11:30am

        assertEquals(expResult, result);

    }

    @Test
    public void testApptShouldOverlap() {
////            if (aStart.isBefore(other.getEnd()) && (aEnd.isAfter(other.getStart()))) {
        //Add initial appointment time to list for Sam on 1/1/20 at 10:00 - 11:30am
        setup();
        a3 = new Appointment();
        a3.setBarber(b);
        a3.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 10, 15));
        a3.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 12, 00));
        appointmentsForBarber.add(a3);
        AppointmentCache.TESTING = appointmentsForBarber;

        Boolean result = appointmentCache.checkAppointmentOverlap(a3);
        Boolean expResult = true;
        //should overlap with initial appointment
        assertEquals(expResult, result);

    }

    @Test
    public void testAdjacentApptShouldNotOverlap() {

        //Add initial appointment time to list for Sam on 1/1/20 at 10:00 - 11:30am
        setup();

//     Add another appointment for Sam on 1/1/20 10:45 to 11:45 am
        a3 = new Appointment();
        a3.setBarber(b);
        a3.setStart(LocalDateTime.of(2020, Month.JANUARY, 1, 9, 30));
        a3.setEnd(LocalDateTime.of(2020, Month.JANUARY, 1, 10, 00));
        appointmentsForBarber.add(a3);
        AppointmentCache.TESTING = appointmentsForBarber;

        Boolean result = appointmentCache.checkAppointmentOverlap(a3);

        Boolean expResult = false;
        //should not overlap with initial appointment
        assertEquals(expResult, result);

    }

}
