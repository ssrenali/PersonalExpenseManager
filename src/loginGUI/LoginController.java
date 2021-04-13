package loginGUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField enterPasswordField;

    private static int accountUserID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }


    public void loginButtonOnAction(ActionEvent event) {

        if (!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            validateLogin();

        } else {
            loginMessageLabel.setText("Please enter login information");
        }
    }
    // make stage close on cancel button
    public void cancelButtonOnAction (ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        // SQL statement
        //find the total count of matching username and password row (should only be 1)
        String verifyLogin = "SELECT count(1), iduser_account FROM user_account WHERE username = '" + usernameTextField.getText() + "' AND password = '" + enterPasswordField.getText() + "'  group by iduser_account";
       // String userIDString = "SELECT iduser_account FROM user_account WHERE username = '" + usernameTextField.getText() + "' AND password = '" + enterPasswordField.getText() + "'";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);
           // ResultSet userIDResult = statement.executeQuery(userIDString);

            while(queryResult.next()){
                // if result returns only 1 count, indicates user exists and password matches
                // if user account exists, get the user ID to global variable that can be
                // referenced later
                if (queryResult.getInt(1) == 1) {
                    // fix - open up PEM
                    loginMessageLabel.setText("Logging you in!");
                    accountUserID = queryResult.getInt(2);
                    //open dashboard
                    openPEM();
                    usernameTextField.clear();
                    enterPasswordField.clear();

                } else {
                    loginMessageLabel.setText("Invalid login. Please try again");
                }
            }
            loginMessageLabel.setText("Invalid login. Please try again");

        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public static int getAccountUserID() {
        return accountUserID;
    }

    public void signUpButtonOnAction(ActionEvent event) {
        createAccountForm();
    }

    public void createAccountForm() {
        try{
            // make user registration window pop up
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Stage registerStage = new Stage();
            registerStage.setTitle("Personal Expense Manager Registration");
            registerStage.setScene(new Scene(root, 600, 525));
            registerStage.show();

        } catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }

    public void openPEM() {
        try{
            // make user registration window pop up
            Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
            Stage registerStage = new Stage();
            registerStage.setTitle("Personal Expense Manager");
            registerStage.setScene(new Scene(root, 1000, 700));
            registerStage.show();

        } catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }
}
