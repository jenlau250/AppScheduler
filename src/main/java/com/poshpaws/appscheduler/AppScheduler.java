/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poshpaws.appscheduler;

import com.poshpaws.appscheduler.cache.AppointmentCache;
import com.poshpaws.appscheduler.cache.BarberCache;
import com.poshpaws.appscheduler.cache.CustomerCache;
import com.poshpaws.appscheduler.cache.PetCache;
import com.poshpaws.appscheduler.model.Appointment;
import com.poshpaws.appscheduler.model.Barber;
import com.poshpaws.appscheduler.model.Customer;
import com.poshpaws.appscheduler.model.User;
import com.poshpaws.appscheduler.util.Loggerutil;
import com.poshpaws.appscheduler.viewcontroller.AppointmentListController;
import com.poshpaws.appscheduler.viewcontroller.Appointment_AddController;
import com.poshpaws.appscheduler.viewcontroller.BarberAddController;
import com.poshpaws.appscheduler.viewcontroller.BarberScreenController;
import com.poshpaws.appscheduler.viewcontroller.CustomerPaneController;
import com.poshpaws.appscheduler.viewcontroller.CustomerScreenController;
import com.poshpaws.appscheduler.viewcontroller.LoginScreenController;
import com.poshpaws.appscheduler.viewcontroller.MainScreenController;
import com.poshpaws.appscheduler.viewcontroller.ReportScreenController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author jlau2
 */
public class AppScheduler extends Application {

    private Stage mainStage;
    @FXML
    BorderPane mainScreen;
    @FXML
    private AnchorPane screen;
    @FXML
    private AnchorPane login;
    private User currUser;

    @Override
    public void start(Stage mainStage) {
        this.mainStage = mainStage;
        this.mainStage.setTitle("Posh Paws Appointment Scheduler");
        this.mainStage.getIcons().add(new Image("/images/icon.png"));

        BarberCache.flush();
        PetCache.flush();
        CustomerCache.flush();
        AppointmentCache.flush();

//        showLoginScreen();
        showMain(currUser);

        Loggerutil.init();
    }

    public void refreshView() {

        BarberCache.flush();
        PetCache.flush();
        CustomerCache.flush();
        AppointmentCache.flush();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        DBHandler.init();

//        connection = DBHandler.getConn();
        launch(args);
//        DBHandler.closeConnection();
    }

    /**
     *
     * @param currentUser
     */
    public void showMain(User currentUser) {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/MainScreen.fxml"));
            mainScreen = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(mainScreen);

            mainStage.setScene(scene);
            // Give the controller access to the main app.
            MainScreenController controller = loader.getController();
            controller.setMenu(this, currentUser);

            mainStage.show();
        } catch (IOException e) {
            e.getCause().printStackTrace();
        }

    }

    public void showLoginScreen() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/LoginScreen.fxml"));
            login = (AnchorPane) loader.load();

            LoginScreenController controller = loader.getController();
            controller.setMainController(this);

            Scene scene = new Scene(login);
            mainStage.setScene(scene);
            mainStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void showCustomerScreen() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/CustomerScreen.fxml"));
            StackPane root = (StackPane) loader.load();
            root.setMinSize(25, 25);
            root.setPrefSize(25000, 25000);
            mainScreen.setCenter(root);
            mainScreen.setLeft(null);
            mainScreen.setRight(null);

            CustomerScreenController controller = loader.getController();
            controller.setMainController(this);
//            controller.setCustomer(customer);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showBarberScreen() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/BarberScreen.fxml"));
            screen = (AnchorPane) loader.load();

            screen.setMinSize(25, 25);
            screen.setPrefSize(25000, 25000);
            mainScreen.setCenter(screen);
            mainScreen.setLeft(null);
            mainScreen.setRight(null);

            BarberScreenController controller = loader.getController();
            controller.setMainController(this);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showBarberAddScreen() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/BarberAdd.fxml"));
            screen = (AnchorPane) loader.load();

            mainScreen.setLeft(screen);
            mainScreen.setRight(null);

            BarberAddController controller = loader.getController();
            controller.setMainController(this, currUser);
//            controller.setSelectedAppointment(selectedAppt);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showBarberAddScreen(Barber selected) {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/BarberAdd.fxml"));
            screen = (AnchorPane) loader.load();

            mainScreen.setLeft(screen);
            mainScreen.setRight(null);

            BarberAddController controller = loader.getController();
            controller.setMainController(this, currUser);
            controller.setSelected(selected);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showReportScreen() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/ReportScreen.fxml"));
            screen = (AnchorPane) loader.load();
            mainScreen.setCenter(screen);
            mainScreen.setLeft(null);
            mainScreen.setRight(null);
            ReportScreenController controller = loader.getController();
            controller.setMainController(this);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showAppointmentListScreen() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/AppointmentList.fxml"));
            screen = (AnchorPane) loader.load();
            screen.setMinSize(25, 25);
            screen.setPrefSize(25000, 25000);
            mainScreen.setCenter(screen);
            mainScreen.setLeft(null);
            mainScreen.setRight(null);

            AppointmentListController controller = loader.getController();
            controller.setMainController(this);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showAppointmentAddScreen() {

        //TO PASS SELECTED APPT FOR EDITING
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/Appointment_Add.fxml"));
            screen = (AnchorPane) loader.load();

            mainScreen.setLeft(screen);

            Appointment_AddController controller = loader.getController();
            controller.setMainController(this);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showAppointmentAddScreen(Appointment selectedAppt) {

        //TO PASS SELECTED APPT FOR EDITING
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/Appointment_Add.fxml"));
            screen = (AnchorPane) loader.load();

            mainScreen.setLeft(screen);

            Appointment_AddController controller = loader.getController();
            controller.setMainController(this);
            controller.setSelectedAppointment(selectedAppt);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showCustomerPane(Customer selCustomer) {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppScheduler.class.getResource("/fxml/CustomerPane.fxml"));
            screen = (AnchorPane) loader.load();

            mainScreen.setRight(screen);

            CustomerPaneController controller = loader.getController();
            controller.setMainController(this);
            controller.setSelected(selCustomer);
//            controller.setSelectedAppointment(selectedAppt);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeBottomBorder() {

        mainScreen.setBottom(null);
    }

    public void closeRightBorder() {

        mainScreen.setRight(null);
    }

    public void closeLeftBorder() {

        mainScreen.setLeft(null);
    }

//        try {
//
//
//
//  //this was used to load scene in front of main appointment list
//            // Load root layout from fxml file.
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/fxml/Appointment_Add.fxml"));
//            AnchorPane rootPane = (AnchorPane) loader.load();
//            Stage stage = new Stage(StageStyle.UNDECORATED);
//            stage.initModality(Modality.APPLICATION_MODAL);
//
//            // Pass main controller reference to view
//            Appointment_AddController controller = loader.getController();
//            controller.setMainController(this);
//
//            // Show the scene containing the root layout.
//            Scene scene = new Scene(rootPane);
//            stage.setScene(scene);
//            stage.show();
//        } catch (IOException ex) {
////            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
//        }
//}
}
