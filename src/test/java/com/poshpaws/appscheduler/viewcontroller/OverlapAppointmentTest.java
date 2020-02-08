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
import java.util.List;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 *
 * @author Jen
 */
public class OverlapAppointmentTest {

    AppointmentCache appointmentCache = new AppointmentCache();
    @Mock
    Appointment appointment = new Appointment();
    @Mock
    Barber b = new Barber("1", "Sam");
    Appointment_AddController appointment_AddController = new Appointment_AddController();

    @Mock
    List<Appointment> mockList = new ArrayList<>();

    @Test
    public void testAppointmentTimes() {

//               List<Appointment> appointmentsForBarber = AppointmentCache.getBarberAppointments(barberId);
        //January 1, 2020, 10 AM to Noon
        LocalDateTime startTime1 = LocalDateTime.of(2020, Month.JANUARY, 1, 10, 00);
        LocalDateTime endTime1 = LocalDateTime.of(2020, Month.JANUARY, 1, 12, 00);

        //Create first appointment
        Appointment appointment1 = Mockito.mock(Appointment.class);
        appointment1.setBarber(b);
        appointment1.setStart(startTime1);
        appointment1.setEnd(endTime1);

        mockList.add(appointment1);

        Mockito.when(AppointmentCache.getBarberAppointments(b.getBarberId())).thenReturn(mockList);

        for (Appointment a : mockList) {
            System.out.println(a);
        }

//        boolean result = appointment_AddController.checkAppointmentOverlap(appointment);
//        assertFalse(result);
    }

}
