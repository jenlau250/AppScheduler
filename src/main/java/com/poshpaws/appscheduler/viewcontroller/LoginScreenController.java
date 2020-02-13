package com.poshpaws.appscheduler.viewcontroller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.poshpaws.appscheduler.dao.DBHandler;
import com.poshpaws.appscheduler.AppScheduler;
import com.poshpaws.appscheduler.model.User;
import com.poshpaws.appscheduler.util.Loggerutil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class LoginScreenController {

    private AppScheduler mainApp;
    private User currentUser;

    @FXML
    private JFXTextField textUserId;
    @FXML
    private JFXPasswordField textUserPw;
    @FXML
    private JFXButton buttonLogin;
    @FXML
    private JFXButton buttonCancel;

    ObservableList<User> Users = FXCollections.observableArrayList();

    User user = new User();

//    // Reference back to main screen
//    ResourceBundle rb = ResourceBundle.getBundle("appscheduler/util/rb");
    private final static Logger logger = Logger.getLogger(Loggerutil.class.getName());

    public LoginScreenController() {

    }

    @FXML
    void handleActionLogin(ActionEvent event) {

// Show error message If user name or password is blank
        String userNameInput = textUserId.getText();
        String userPwInput = textUserPw.getText();

        // EXCEPTION CONTROL: Entering incorrect username and password
        if ((textUserId.getText().length() == 0) || (textUserPw.getText().length() == 0)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Login Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter user name and password to login.");
            alert.showAndWait();

        } else {

            User validateUser = validateLogin(userNameInput, userPwInput);
            if (validateUser == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login attempt was unsuccessful");
                alert.setHeaderText(null);
                alert.setContentText("Please try to login again.");
                alert.showAndWait();

            } else {

                //save user preferences
                Preferences userPreferences = Preferences.userRoot();
                userPreferences.put("username", textUserId.getText());
//
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle(rb.getString("SUCCESSFUL LOGIN"));
//                alert.setHeaderText(null);
//                alert.setContentText(rb.getString("LOGIN WAS SUCCESSFUL."));
//                alert.showAndWait();

                mainApp.showMain(validateUser);

                //Log user login name and timestamp
                logger.log(Level.INFO, userNameInput + " logged in on " + Loggerutil.currentTimestamp());

            }
        }
    }

    User validateLogin(String username, String password) {
        try {
            PreparedStatement ps = DBHandler.getConn().prepareStatement("SELECT * FROM user WHERE userName=? and password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user.setUserName(rs.getString("userName"));
                user.setPassword(rs.getString("password"));
                user.setUserId(rs.getInt("userId"));
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;

    }

    /**
     * Initializes the controller class.
     *
     * @param mainApp
     * @param currentUser
     */
    public void setMainController(AppScheduler mainApp) {

        this.mainApp = mainApp;
//        this.currentUser = currentUser;
        buttonLogin.setText("Login");
        buttonCancel.setText("Cancel");

//	Lambda use - set exit action to cancel button
        buttonCancel.setOnAction((evt) -> {
            System.exit(0);
        });

    }

}
