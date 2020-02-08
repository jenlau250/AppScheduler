/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.dao;

import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Barber;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.Pet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.*;
import org.mockito.*;
import org.mockito.junit.*;

/**
 *
 * @author Jen
 */
public class AppointmentServiceTest {

    @Mock
    private AppointmentDaoImpl appointmentDaoImpl;
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void testFind() {
        MockitoAnnotations.initMocks(this);
        AppointmentService as = new AppointmentService(appointmentDaoImpl);
        as.findbyId("1");
        Mockito.verify(appointmentDaoImpl).findbyId("1");
    }

    @Test
    public void initTest() {

        Barber b = new Barber();

        Customer cus = new Customer();
        Pet p = new Pet();

        AppointmentService s = new AppointmentService(appointmentDaoImpl);
        Mockito.when(appointmentDaoImpl.findbyId("1"))
                .thenReturn(testContact());
        Appointment c = s.findbyId("1");
        Assert.assertEquals("1", c.getAppointmentId());
        Assert.assertEquals(LocalDate.of(2016, 1, 1), c.getStartDate());
        Assert.assertEquals(LocalDateTime.of(2016, Month.JANUARY, 1, 22, 20), c.getStart());
        Assert.assertEquals(LocalDateTime.of(2016, Month.JANUARY, 1, 22, 20), c.getEnd());
        Assert.assertEquals("Description", c.getDescription());
        Assert.assertEquals("Full Service", c.getType());
        Assert.assertEquals(b, c.getBarber());
        Assert.assertEquals(cus, c.getCustomer());
        Assert.assertEquals(p, c.getPet());

        Mockito.verify(appointmentDaoImpl).findbyId("1");
    }

//   public Appointment(String appointmentId, LocalDate startDate, LocalDateTime start, LocalDateTime end, String desc, String type, Barber barber, Customer customer, Pet pet) {
    public Appointment testContact() {
        Barber b = new Barber();
        Customer cus = new Customer();
        Pet p = new Pet();
        Appointment appt = new Appointment("1", LocalDate.of(2016, 1, 1), LocalDateTime.of(2016, Month.JANUARY, 1, 22, 20),
                LocalDateTime.of(2016, Month.JANUARY, 1, 22, 20), "Description", "Full Service", b, cus, p);
        return appt;
    }
}
