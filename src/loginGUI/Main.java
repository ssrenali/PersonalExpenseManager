package loginGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Person Expense Manager program is an application that keeps track of a user's
 * income and expenses and creates yearly, monthly and weekly reports.
 *
 * @author  Serena Li
 * @since   2021-04-13
 */

public class Main extends Application {

    /**
     * Sets up login page as the primary stage and opens it up
     *
     * @param primaryStage stage that is being opened up
     */

    @Override
    public void start(Stage primaryStage) throws Exception{
        // opens up login form
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Personal Expense Manager");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    /**
     * Main method of the application
     *
     * @param args The command line arguments
     */

    public static void main(String[] args) {
        // launch program
        launch(args);
    }
}
