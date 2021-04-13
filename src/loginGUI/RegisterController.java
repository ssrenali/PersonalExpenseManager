package loginGUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Statement;

public class RegisterController {

    @FXML
    private Button closeButton;
    @FXML
    private Label registrationMessageLabel;
    @FXML
    private PasswordField setPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField userNameTextField;


    public void registerButtonOnAction(ActionEvent event) {

        // booleans to check if all registration info is there
        boolean cond1 = !firstNameTextField.getText().isBlank() && !lastNameTextField.getText().isBlank();
        boolean cond2 = !userNameTextField.getText().isBlank() && !setPasswordField.getText().isBlank();

        if (setPasswordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordLabel.setText("");
            if (cond1 && cond2) {
                registerUser();
            } else {
                // message label that says please enter all info
                registrationMessageLabel.setText("Please enter all required information.");
            }


        } else {
            confirmPasswordLabel.setText("Passwords do not match!");
        }
    }

    public void closeButtonOnAction(ActionEvent event) {
        // closes stage that closeButton is on
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        //Platform.exit();
    }

    public void registerUser() {

        // database connection
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String username = userNameTextField.getText();
        String password = setPasswordField.getText();

        // string concatentation for sql query code

        String insertFields = "INSERT INTO user_account(lastname, firstname, username, password) VALUES('";
        String insertValues = firstName + "','" + lastName + "','" + username + "','" + password + "')";
        String insertToRegister = insertFields + insertValues;

        try{
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);
            // added to database
            registrationMessageLabel.setText("User has been registered successfully!");

        }catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }


    }


}
