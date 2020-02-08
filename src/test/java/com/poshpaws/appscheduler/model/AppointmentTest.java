/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.model;

import com.poshpaws.appscheduler.cache.AppointmentCache;
import com.poshpaws.appscheduler.dao.AppointmentDaoImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 *
 * @author Jen
 */
public class AppointmentTest {

    /**
     * Do not change this method! The tests will fail due to overlapping
     * meetings. Implement the logic in MyCalendar to check for overlapping
     * meetings.
     *
     * @see MyCalendar#hasOverlappingMeeting(LocalDate, LocalTime, Duration) )
     */
    //https://github.com/BNYMellon/CodeKatas/blob/master/calendar-kata/src/test/java/bnymellon/codekatas/calendarkata/MyCalendarTest.java
    private Appointment appointment;

    private AppointmentDaoImpl dao;

    @Mock
    private List<Appointment> appointments; //not mocked
//https://www.javatips.net/api/jfxtras-master/jfxtras-8.0/jfxtras-icalendarfx/src/test/java/jfxtras/icalendarfx/component/ScheduleConflictTest.java#
    AppointmentCache appointmentCache = new AppointmentCache();

    @Mock
    Barber barber;
    @Mock
    Appointment a1;

    @Mock
    Appointment a2;

    public AppointmentTest() {
    }

    @Before
    public void setUp() {
        //initialize appointment list
        //method to add appointment1
        //method to add overlapping appointment2
        //method to add adjacent appointment
        this.barber = new Barber("1", "Sam");
        this.appointment = new Appointment();
        this.appointments = new ArrayList<>();

        //setup initial appointment
    }

    Appointment createAppointment(LocalDateTime start, LocalDateTime end, Barber b) {
        Appointment appt = new Appointment();
        appt.setStart(start);
        appt.setEnd(end);
        appt.setBarber(b);
        return new Appointment();

    }

    @Test
    public void setupInitialAppointment() {
        //should pass
        LocalDateTime startTime1 = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 00);
        LocalDateTime endTime1 = LocalDateTime.of(2020, Month.JANUARY, 1, 12, 00);
        String barberId = barber.getBarberId();

        this.appointment.setBarber(barber);
        this.appointment.setStart(startTime1);
        this.appointment.setEnd(endTime1);
//        List<Appointment> list = Arrays.asList(appointment);

        this.appointments.add(appointment);
        Boolean expected = true;
//        Boolean actual = appointmentCache.checkAppointmentOverlap(startTime1, endTime1, barberId);
//        Assert.assertEquals(expected, actual);
    }

    @Test
    public void printAppointments() {
        System.out.println(this.appointments);
    }

    /**
     * Implement the
     * {@link MyCalendar#hasOverlappingMeeting(LocalDate, LocalTime, Duration)}
     * method. AppointmentCache #validateOverlapAppointment(LocalDateTime a,
     * LocalDateTime b, Barber)
     */
//    @Test
//    public void hasOverlappingMeeting() {
//        Assert.assertTrue(this.calendar.hasOverlappingMeeting(
//                LocalDate.of(2017, 7, 7),
//                LocalTime.NOON,
//                Duration.ofHours(1)));
//    }
//
    public void tearDown() {
    }

}
