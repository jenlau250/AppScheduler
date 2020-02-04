/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.poshpaws.appscheduler.dao.DBConnection;
import com.poshpaws.appscheduler.jCalendar;
import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Pet;
import com.poshpaws.appscheduler.model.User;
import com.poshpaws.appscheduler.util.DateTimeUtil;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Jen
 */
public class CustomerPaneController {

    // Controllers
//    private AppointmentScreenController mainController;
    private jCalendar mainApp;
    private User currentUser;
    private Appointment selectedAppt;
    @FXML
    private Label topLabel;

    @FXML
    private ImageView btnClose;

    @FXML
    private Label apptIdLabel;

    @FXML
    private ImageView petPhoto;

    @FXML
    private JFXTextField txtCustomerName;

    @FXML
    private JFXTextField txtCustomerPhone;

    @FXML
    private JFXTextField txtCustomerEmail;

    @FXML
    private JFXTextField txtNewPet;

    @FXML
    private JFXTextField txtPetDesc;

    @FXML
    private JFXTextField txtPetType;

    @FXML
    private JFXTextField txtNotes;

    @FXML
    private JFXButton btnCustSave;

    @FXML
    private JFXButton btnCustDel;
    @FXML
    private JFXButton btnPetSave;
    @FXML
    private JFXButton btnPetDel;
    @FXML
    private JFXButton btnUpload;

    private Pet selectedPet;

    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

//    Customer customer;
    /**
     * Initializes the controller class.
     *
     * @param mainApp
     * @param currentUser
     */
    public void setMainController(jCalendar mainApp, User currentUser) {

        this.mainApp = mainApp;
        this.currentUser = currentUser;

//        //CLOSE PANE ON EXIT
//        System.out.println("running initialize()");
        btnClose.setOnMouseClicked((evt) -> {
            mainApp.closeRightBorder();
        });

    }

    @FXML
    private void showCustomerDetails(Appointment appt) {

        apptIdLabel.setText(appt.getAppointmentId());
        String startTime = DateTimeUtil.parseTimeToStringFormat(appt.getStart().toLocalTime());

        txtCustomerName.setText(appt.getCustomer().getCustomerName());
        txtCustomerPhone.setText(appt.getCustomer().getPhone());
        txtCustomerEmail.setText(appt.getCustomer().getEmail());
        txtNotes.setText(appt.getCustomer().getNotes());
        txtNewPet.setText(appt.getPet().getPetName());
        txtPetDesc.setText(appt.getPet().getPetDesc());
        txtPetType.setText(appt.getPet().getPetType());
        selectedPet = appt.getPet();
//        Pet pet = PetCache.getPet(appt.getPet().getPetId());
//        loadPetPhoto(appt.getPet());

        imageRetrievalService.restart();

        petPhoto.imageProperty().bind(imageRetrievalService.valueProperty());
//can't center image photo
// requires getting a pet id somewhere
        centerImage();
    }

    public void centerImage() {
        Image img = petPhoto.getImage();

        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = petPhoto.getFitWidth() / img.getWidth();
            double ratioY = petPhoto.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if (ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            petPhoto.setX((petPhoto.getFitWidth() - w) / 2);
            petPhoto.setY((petPhoto.getFitHeight() - h) / 2);

        }
    }

    @FXML
    void handleApptCancel(ActionEvent event) {
        mainApp.closeRightBorder();
    }

    public void setSelectedAppointment(Appointment appt) {
        //initialize update fields here
        topLabel.setText("Edit Appointment Details");
//        choiceNewCustomer.setDisable(true);

        showCustomerDetails(appt);

    }

    public Image getImageById(String id) throws SQLException, IOException {

        try (
                PreparedStatement ps = DBConnection.getConn().prepareStatement(
                        "SELECT image FROM images WHERE petId = ?");) {

            ps.setString(1, id);
            ResultSet results = ps.executeQuery();
            Image img = null;
            if (results.next()) {
                Blob foto = results.getBlob("image");
                InputStream is = foto.getBinaryStream();
                img = new Image(is);
                is.close();
            }
            results.close();
            return img;
        }
    }

    private final Service<Image> imageRetrievalService = new Service<Image>() {
        @Override
        protected Task<Image> createTask() {
            final String id;

            if (selectedPet == null) {
                id = null;
            } else {
                id = selectedPet.getPetId();
            }
            return new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    if (id == null) {
                        return null;
                    }
                    return getImageById(id);
                }
            };
        }
    };

    //constructor is called first, The initialize method is called after all @FXML annotated members have been injected
    @FXML
    public void initialize() {

    }

}
