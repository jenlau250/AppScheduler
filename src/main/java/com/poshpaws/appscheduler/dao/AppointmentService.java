/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.dao;

import com.poshpaws.appscheduler.model.Appointment;

/**
 *
 * @author Jen
 */
public class AppointmentService {

    private AppointmentDaoImpl appointmentDaoImpl;

    public AppointmentService(AppointmentDaoImpl appointmentDaoImpl) {
        this.appointmentDaoImpl = appointmentDaoImpl;
    }

    public Appointment findbyId(String id) {
        return appointmentDaoImpl.findbyId(id);
    }

}
